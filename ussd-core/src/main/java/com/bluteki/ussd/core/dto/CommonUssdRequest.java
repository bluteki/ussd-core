package com.bluteki.ussd.core.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class CommonUssdRequest {
    private String msisdn;
    private String sessionId;
    private String input;
    private String requestType;
    private String operator;
    private Map<String, String> additionalParams = new HashMap<>();
    private String currentMenu;

    public Map<String, String> getAdditionalParams() {
        if (additionalParams == null) {
            additionalParams = new HashMap<>();
        }
        return additionalParams;
    }
}