package com.bluteki.ussd.core.service.operator;

import java.util.Map;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;
import org.springframework.stereotype.Service;



@Service
public class TruRouteOperatorService implements OperatorService {
    @Override
    public CommonUssdResponse processRequest(CommonUssdRequest request) {

        CommonUssdResponse response = new CommonUssdResponse();
        response.setMsisdn(request.getMsisdn());
        response.setSessionId(request.getSessionId());

        boolean releaseSession = "3".equals(request.getRequestType());
        response.setReleaseSession(releaseSession);

        response.setOperatorSpecificParams(Map.of(
                "type", request.getRequestType(),
                "premium", "false"));

        return response;
    }

    @Override
    public boolean supports(String operator) {
        return "TRUROUTE".equalsIgnoreCase(operator);
    }
}