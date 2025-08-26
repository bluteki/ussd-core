package com.bluteki.ussd.core.service.operator;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;

@Service
public class MovitelOperatorService implements OperatorService {
    @Override
    public CommonUssdResponse processRequest(CommonUssdRequest request) {

        CommonUssdResponse response = new CommonUssdResponse();
        
        response.setMsisdn(request.getMsisdn());
        response.setSessionId(request.getSessionId());

        boolean releaseSession = "203".equals(request.getAdditionalParams().get("requestType"));
        response.setReleaseSession(releaseSession);

        response.setOperatorSpecificParams(Map.of(
                "errorCode", "0",
                "requestType", request.getAdditionalParams().getOrDefault("requestType", "202")));

        return response;
    }

    @Override
    public boolean supports(String operator) {
        return "MOVITEL".equalsIgnoreCase(operator);
    }
}