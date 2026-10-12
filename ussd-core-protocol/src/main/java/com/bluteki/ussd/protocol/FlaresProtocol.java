package com.bluteki.ussd.protocol;

import java.util.Map;

/**
 * Protocolo sincrono da Flares: pedido por parametros de formulario ({@code MSISDN}, {@code SessionId},
 * {@code NewRequest}, {@code SubscriberInput}); resposta em texto, {@code "FC "} para continuar e {@code "FB "}
 * para terminar.
 */
public final class FlaresProtocol {

    public static final String CONTINUE = "FC ";
    public static final String END = "FB ";

    private FlaresProtocol() {
    }

    public record Request(String msisdn, String sessionId, boolean newRequest, String input) {

        public Phase phase() {
            return newRequest ? Phase.START : Phase.CONTINUE;
        }
    }

    public static Request fromParams(Map<String, String> params) {
        String sessionId = params.get("SessionId");
        String newRequest = params.get("NewRequest");
        if (sessionId == null || sessionId.isBlank()) {
            throw new ProtocolException("Pedido Flares sem SessionId");
        }
        if (!"true".equalsIgnoreCase(newRequest) && !"false".equalsIgnoreCase(newRequest)) {
            throw new ProtocolException("NewRequest invalido: " + newRequest);
        }
        return new Request(params.get("MSISDN"), sessionId, Boolean.parseBoolean(newRequest.toLowerCase()),
                params.getOrDefault("SubscriberInput", ""));
    }

    public static String response(String message, boolean end) {
        return (end ? END : CONTINUE) + (message == null ? "" : message);
    }

    /** Le a resposta de uma aplicacao Flares. Respostas sem prefixo sao tratadas como finais. */
    public static Reply parseResponse(String body) {
        String text = body == null ? "" : body;
        if (text.startsWith(CONTINUE)) {
            return new Reply(text.substring(CONTINUE.length()), false);
        }
        if (text.startsWith(END)) {
            return new Reply(text.substring(END.length()), true);
        }
        return new Reply(text, true);
    }

    public record Reply(String message, boolean end) {
    }
}
