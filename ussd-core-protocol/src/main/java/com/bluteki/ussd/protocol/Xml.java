package com.bluteki.ussd.protocol;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** Leitura de XML tolerante a prefixos/namespaces e protegida contra XXE. */
final class Xml {

    private Xml() {
    }

    static Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            return builder.parse(new InputSource(new StringReader(repair(xml))));
        } catch (Exception e) {
            throw new ProtocolException("XML invalido: " + e.getMessage(), e);
        }
    }

    /** Texto do primeiro elemento com este nome local, ignorando o prefixo; {@code null} se nao existir. */
    static String text(Document doc, String localName) {
        NodeList nodes = doc.getElementsByTagNameNS("*", localName);
        if (nodes.getLength() == 0) {
            return null;
        }
        String value = nodes.item(0).getTextContent();
        return value == null ? null : value.trim();
    }

    static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (char c : value.toCharArray()) {
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&apos;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Corrige defeitos comuns no XML que circula entre operadoras e aplicacoes: {@code <br>} por fechar dentro
     * do texto, atributos sem espaco entre eles e prefixos nao declarados.
     */
    private static String repair(String xml) {
        String fixed = xml.trim()
                .replaceAll("(?i)<br\\s*/?>", "&#10;")
                .replaceAll("\"(xmlns:)", "\" $1");
        return fixUndeclaredPrefixes(fixed);
    }

    /**
     * A Movitel envia por vezes o prefixo {@code urn:} sem o declarar (declara {@code xsd} no mesmo URI) e
     * {@code xsi:type} sem declarar {@code xsi}. Declara-os no elemento raiz para o XML ser valido.
     */
    private static String fixUndeclaredPrefixes(String xml) {
        String fixed = xml;
        for (String prefix : new String[] { "urn", "xsi", "tem" }) {
            if (fixed.contains("<" + prefix + ":") || fixed.contains(" " + prefix + ":")) {
                if (!fixed.contains("xmlns:" + prefix + "=")) {
                    String uri = "xsi".equals(prefix) ? "http://www.w3.org/2001/XMLSchema-instance"
                            : "urn:bluteki:undeclared:" + prefix;
                    fixed = fixed.replaceFirst("<([A-Za-z]+:)?Envelope", "<$1Envelope xmlns:" + prefix + "=\"" + uri + "\"");
                }
            }
        }
        return fixed;
    }
}
