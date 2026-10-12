package com.bluteki.ussd.protocol;

import org.w3c.dom.Document;

/**
 * Protocolo SOAP assincrono da Movitel (gateway Viettel). A Movitel envia o pedido; o parceiro responde logo
 * com {@link #ack(String)} e depois envia o menu para o gateway com {@link #menu}.
 */
public final class MovitelProtocol {

    public static final String START = "100";
    public static final String INPUT = "101";
    public static final String CANCEL = "102";
    public static final String ERROR = "104";
    public static final String MENU = "202";
    public static final String FINAL = "203";

    /** errorCode 0: recebido com sucesso. */
    public static final String OK = "0";
    /** errorCode -1: erro do sistema. */
    public static final String SYSTEM_ERROR = "-1";
    /** errorCode 1: user/pass/ip nao correspondem. */
    public static final String AUTH_ERROR = "1";

    private MovitelProtocol() {
    }

    public record Request(
            String user,
            String pass,
            String msisdn,
            String message,
            String serviceCode,
            String sessionId,
            String transactionId,
            String requestType,
            String gatewayId,
            String imsi) {

        public Phase phase() {
            return switch (requestType == null ? "" : requestType) {
                case START -> Phase.START;
                case INPUT -> Phase.CONTINUE;
                case CANCEL, ERROR -> Phase.END;
                default -> throw new ProtocolException("requestType desconhecido: " + requestType);
            };
        }

        /** Codigo marcado no pedido inicial: vem em {@code service_code} ou, em versoes antigas, em {@code msg}. */
        public String dialedCode() {
            return serviceCode != null && !serviceCode.isBlank() ? serviceCode : message;
        }
    }

    public static Request parse(String xml) {
        Document doc = Xml.parse(xml);
        String sessionId = Xml.text(doc, "session_id");
        if (sessionId == null) {
            sessionId = Xml.text(doc, "sessionid");
        }
        Request request = new Request(Xml.text(doc, "user"), Xml.text(doc, "pass"), Xml.text(doc, "msisdn"),
                Xml.text(doc, "msg"), Xml.text(doc, "service_code"), sessionId, Xml.text(doc, "transactionid"),
                Xml.text(doc, "requestType"), Xml.text(doc, "ussdgw_id"), Xml.text(doc, "imsi"));
        if (request.transactionId() == null || request.transactionId().isBlank()) {
            throw new ProtocolException("Pedido Movitel sem transactionid");
        }
        return request;
    }

    /** Resposta HTTP imediata ao pedido da Movitel. */
    public static String ack(String errorCode) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body><ussdresponse><errorCode>" + Xml.escape(errorCode) + "</errorCode></ussdresponse>"
                + "</soap:Body></soap:Envelope>";
    }

    /**
     * Pedido enviado ao gateway da Movitel com o ecra a mostrar. As quebras de linha passam a {@code <br>},
     * como o gateway espera; o resto do texto e escapado.
     */
    public static String menu(Request request, String message, boolean finalScreen, String user, String pass) {
        String text = Xml.escape(message == null ? "" : message).replace("\r\n", "\n").replace("\n", "<br>");
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:urn=\"http://viettel.com/xsd\">"
                + "<soapenv:Header/><soapenv:Body><urn:start>"
                + "<user>" + Xml.escape(user) + "</user>"
                + "<pass>" + Xml.escape(pass) + "</pass>"
                + "<msg>" + text + "</msg>"
                + "<sessionid>" + Xml.escape(request.sessionId()) + "</sessionid>"
                + "<transactionid>" + Xml.escape(request.transactionId()) + "</transactionid>"
                + "<requestType>" + (finalScreen ? FINAL : MENU) + "</requestType>"
                + "<ussdgw_id>" + Xml.escape(request.gatewayId()) + "</ussdgw_id>"
                + "</urn:start></soapenv:Body></soapenv:Envelope>";
    }

    /**
     * Pedido no formato da Movitel, para reencaminhar uma sessao a uma aplicacao que fala este protocolo
     * (o Studio faz de gateway).
     */
    public static String request(Request request) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:urn=\"http://viettel.com/xsd\">"
                + "<soapenv:Header/><soapenv:Body><urn:start>"
                + "<user>" + Xml.escape(request.user()) + "</user>"
                + "<pass>" + Xml.escape(request.pass()) + "</pass>"
                + "<msisdn>" + Xml.escape(request.msisdn()) + "</msisdn>"
                + "<msg>" + Xml.escape(request.message()) + "</msg>"
                + "<service_code>" + Xml.escape(request.serviceCode()) + "</service_code>"
                + "<session_id>" + Xml.escape(request.sessionId()) + "</session_id>"
                + "<transactionid>" + Xml.escape(request.transactionId()) + "</transactionid>"
                + "<requestType>" + Xml.escape(request.requestType()) + "</requestType>"
                + "<ussdgw_id>" + Xml.escape(request.gatewayId()) + "</ussdgw_id>"
                + "</urn:start></soapenv:Body></soapenv:Envelope>";
    }

    /** errorCode da resposta imediata ({@link #ack}); {@code null} se nao existir. */
    public static String ackCode(String xml) {
        return Xml.text(Xml.parse(xml), "errorCode");
    }

    /** Converte o {@code msg} de um menu Movitel (com {@code <br>}) para texto com quebras de linha. */
    public static String menuText(String msg) {
        return msg == null ? "" : msg.replaceAll("(?i)<br\\s*/?>", "\n").trim();
    }
}
