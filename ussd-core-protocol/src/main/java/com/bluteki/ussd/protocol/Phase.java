package com.bluteki.ussd.protocol;

/** Momento da sessao USSD, comum a todas as operadoras. */
public enum Phase {
    /** O utilizador marcou o codigo. */
    START,
    /** O utilizador respondeu a um ecra. */
    CONTINUE,
    /** A operadora terminou a sessao (cancelamento, expiracao ou erro). */
    END
}
