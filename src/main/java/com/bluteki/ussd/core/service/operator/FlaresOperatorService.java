package com.bluteki.ussd.core.service.operator;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;

@Service
public class FlaresOperatorService implements OperatorService {
    @Override
    public CommonUssdResponse processRequest(CommonUssdRequest request) {
      
        
        CommonUssdResponse response = new CommonUssdResponse();
        response.setMsisdn(request.getMsisdn());
        response.setSessionId(request.getSessionId());
        
        
        boolean releaseSession = "FB".equals(request.getAdditionalParams().get("Freeflow"));
        response.setReleaseSession(releaseSession);
        
        response.setOperatorSpecificParams(Map.of(
            "Freeflow", "FC"
        ));
        
        return response;
    }

    @Override
    public boolean supports(String operator) {
        return "FLARES".equalsIgnoreCase(operator);
    }
}