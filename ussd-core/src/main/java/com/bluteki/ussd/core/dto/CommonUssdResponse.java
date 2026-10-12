package com.bluteki.ussd.core.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class CommonUssdResponse {
    private String msisdn;
    private String sessionId;
    private String message;
    private boolean releaseSession;
    private Map<String, String> operatorSpecificParams = new HashMap<>();
    private String nextMenu;

    public Map<String, String> getOperatorSpecificParams() {
        if (operatorSpecificParams == null) {
            operatorSpecificParams = new HashMap<>();
        }
        return operatorSpecificParams;
    }
}