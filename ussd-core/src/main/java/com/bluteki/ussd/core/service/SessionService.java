package com.bluteki.ussd.core.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bluteki.ussd.core.model.UssdSession;
import com.bluteki.ussd.core.model.UssdTransaction;
import com.bluteki.ussd.core.repository.SessionRepository;
import com.bluteki.ussd.core.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final TransactionRepository transactionRepository;

    public SessionService(SessionRepository sessionRepository, 
                        TransactionRepository transactionRepository) {
        this.sessionRepository = sessionRepository;
        this.transactionRepository = transactionRepository;
    }

    public UssdSession getOrCreateSession(String sessionId, String msisdn, String operator) {
        log.info("Getting or creating session for ID: {}, MSISDN: {}", sessionId, msisdn);
        return sessionRepository.findById(sessionId)
            .orElseGet(() -> {
                UssdSession newSession = new UssdSession();
                newSession.setSessionId(sessionId);
                newSession.setMsisdn(msisdn);
                newSession.setOperator(operator);
                newSession.setCreatedAt(LocalDateTime.now());
                newSession.setLastUpdated(LocalDateTime.now());
                newSession.setActive(true);
                log.debug("Creating new session: {}", newSession);
                return sessionRepository.save(newSession);
            });
    }

    public Optional<UssdSession> getSession(String sessionId) {
        log.debug("Getting session by ID: {}", sessionId);
        return sessionRepository.findById(sessionId);
    }

    public void saveSession(UssdSession session) {
        session.setLastUpdated(LocalDateTime.now());
        log.debug("Saving session: {}", session);
        sessionRepository.save(session);
    }

    public void endSession(String sessionId) {
        log.info("Ending session: {}", sessionId);
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setActive(false);
            session.setLastUpdated(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }

    public void logTransaction(UssdSession session, String input, String response, String requestType) {
        UssdTransaction transaction = new UssdTransaction();
        transaction.setSession(session);
        transaction.setInput(input);
        transaction.setResponse(response);
        transaction.setRequestType(requestType);
        transaction.setTimestamp(LocalDateTime.now());
        log.debug("Logging transaction: {}", transaction);
        transactionRepository.save(transaction);
    }
}
