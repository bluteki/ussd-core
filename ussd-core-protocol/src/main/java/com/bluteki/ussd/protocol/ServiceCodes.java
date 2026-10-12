package com.bluteki.ussd.protocol;

/** Normalizacao de codigos USSD marcados, para comparar "*612*777#", "612*777" e "*612*777" como iguais. */
public final class ServiceCodes {

    private ServiceCodes() {
    }

    /** Remove espacos, o {@code *} inicial e o {@code #} final: {@code "*612*777#"} passa a {@code "612*777"}. */
    public static String normalize(String code) {
        if (code == null) {
            return "";
        }
        String value = code.replace(" ", "").trim();
        while (value.startsWith("*")) {
            value = value.substring(1);
        }
        while (value.endsWith("#")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    /** Forma canonica para mostrar: {@code "612*777"} passa a {@code "*612*777#"}. */
    public static String display(String code) {
        String normalized = normalize(code);
        return normalized.isEmpty() ? "" : "*" + normalized + "#";
    }

    /**
     * Indica se o codigo marcado corresponde ao codigo registado, aceitando sub-codigos:
     * {@code "612*777*1"} corresponde a {@code "612*777"}.
     */
    public static boolean matches(String dialed, String registered) {
        String d = normalize(dialed);
        String r = normalize(registered);
        return !r.isEmpty() && (d.equals(r) || d.startsWith(r + "*"));
    }
}
