package com.bluteki.ussd.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;
import com.bluteki.ussd.core.model.UssdSession;
import com.bluteki.ussd.core.service.operator.OperatorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class BaseUssdProcessorService implements UssdProcessor {
    
    protected final SessionService sessionService;
    protected final List<OperatorService> operatorServices;
    
    protected BaseUssdProcessorService(SessionService sessionService, 
                                     List<OperatorService> operatorServices) {
        this.sessionService = sessionService;
        this.operatorServices = operatorServices;
    }
    
    @Override
    public CommonUssdResponse handleInitialRequest(CommonUssdRequest request) {
        log.info("Processing initial request for session: {}", request.getSessionId());
        try {
            UssdSession session = sessionService.getOrCreateSession(
                request.getSessionId(), 
                request.getMsisdn(), 
                request.getOperator()
            );
            
            CommonUssdResponse response = processRequest(request, true);
            sessionService.logTransaction(session, request.getInput(), response.getMessage(), "INITIAL");
            
            return enhanceResponseWithOperatorParams(response, request);
            
        } catch (Exception e) {
            log.error("Error processing initial request", e);
            return createErrorResponse("System error occurred");
        }
    }
    
    @Override
    public CommonUssdResponse handleUserInput(CommonUssdRequest request) {
        log.info("Processing user input for session: {}", request.getSessionId());
        try {
            UssdSession session = sessionService.getSession(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
            
            CommonUssdResponse response = processRequest(request, false);
            sessionService.logTransaction(session, request.getInput(), response.getMessage(), "USER_INPUT");
            
            return enhanceResponseWithOperatorParams(response, request);
            
        } catch (Exception e) {
            log.error("Error processing user input", e);
            return createErrorResponse("System error occurred");
        }
    }
    
    protected abstract CommonUssdResponse processRequest(CommonUssdRequest request, boolean isInitial);
    
    protected OperatorService getOperatorService(String operator) {
        return operatorServices.stream()
                .filter(service -> service.supports(operator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported operator: " + operator));
    }
    
    private CommonUssdResponse enhanceResponseWithOperatorParams(CommonUssdResponse response, CommonUssdRequest request) {
        OperatorService operatorService = getOperatorService(request.getOperator());
        CommonUssdResponse operatorResponse = operatorService.processRequest(request);

        if (!response.isReleaseSession()) {
            response.setReleaseSession(operatorResponse.isReleaseSession());
        }
        response.getOperatorSpecificParams().putAll(operatorResponse.getOperatorSpecificParams());
        
        return response;
    }
    
    protected CommonUssdResponse createErrorResponse(String message) {
        CommonUssdResponse response = new CommonUssdResponse();
        response.setMessage(message);
        response.setReleaseSession(true);
        return response;
    }
}