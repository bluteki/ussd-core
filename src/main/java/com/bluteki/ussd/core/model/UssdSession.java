package com.bluteki.ussd.core.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluteki.ussd.core.util.HashMapConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "ussd_sessions")
public class UssdSession {
    @Id
    private String sessionId;
    private String msisdn;
    private String currentMenu;
    private String previousMenu;
    private String operator;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private boolean isActive;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<UssdTransaction> transactions;

        @Column(columnDefinition = "TEXT")
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> additionalParams = new HashMap<>();
}