package com.bluteki.ussd.protocol;

import java.util.Map;

import org.w3c.dom.Document;

/**
 * Protocolo sincrono da TruRoute (TruTeq): pedido em XML ({@code <ussd>}) ou por parametros GET, resposta em XML.
 * No pedido inicial, {@code msg} traz o codigo marcado sem {@code *} inicial nem {@code #} final.
 */
public final class TruRouteProtocol {

    public static final String START = "1";
    public static final String INPUT = "2";
    public static final String RELEASE = "3";
    public static final String TIMEOUT = "4";

    private TruRouteProtocol() {
    }

    public record Request(String msisdn, String sessionId, String type, String message) {

        public Phase phase() {
            return switch (type == null ? "" : type) {
                case START -> Phase.START;
                case INPUT -> Phase.CONTINUE;
                case RELEASE, TIMEOUT -> Phase.END;
                default -> throw new ProtocolException("type desconhecido: " + type);
            };
        }
    }

    public static Request parseXml(String xml) {
        Document doc = Xml.parse(xml);
        return validated(new Request(Xml.text(doc, "msisdn"), Xml.text(doc, "sessionid"), Xml.text(doc, "type"),
                Xml.text(doc, "msg")));
    }

    public static Request fromParams(Map<String, String> params) {
        return validated(new Request(params.get("msisdn"), params.get("sessionid"), params.get("type"),
                params.get("msg")));
    }

    public static String response(String msisdn, String sessionId, String message, boolean end) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ussd>"
                + "<msisdn>" + Xml.escape(msisdn) + "</msisdn>"
                + "<sessionid>" + Xml.escape(sessionId) + "</sessionid>"
                + "<type>" + (end ? RELEASE : INPUT) + "</type>"
                + "<msg>" + Xml.escape(message) + "</msg>"
                + "</ussd>";
    }

    /** Le a resposta XML de uma aplicacao TruRoute: texto e se a sessao termina. */
    public static Reply parseResponse(String xml) {
        Document doc = Xml.parse(xml);
        String type = Xml.text(doc, "type");
        return new Reply(Xml.text(doc, "msg") == null ? "" : Xml.text(doc, "msg"), RELEASE.equals(type));
    }

    public record Reply(String message, boolean end) {
    }

    private static Request validated(Request request) {
        if (request.sessionId() == null || request.sessionId().isBlank()) {
            throw new ProtocolException("Pedido TruRoute sem sessionid");
        }
        request.phase();
        return request;
    }
}
