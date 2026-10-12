package com.bluteki.ussd.protocol;

/** Pedido da operadora que nao segue o formato esperado. */
public class ProtocolException extends RuntimeException {

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
