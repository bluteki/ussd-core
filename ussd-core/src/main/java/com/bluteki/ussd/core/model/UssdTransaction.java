package com.bluteki.ussd.core.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "ussd_transactions")
public class UssdTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String input;
    private String response;
    private LocalDateTime timestamp;
    private String requestType;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @ToString.Exclude
    private UssdSession session;
}