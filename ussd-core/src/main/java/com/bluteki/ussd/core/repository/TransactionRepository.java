package com.bluteki.ussd.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bluteki.ussd.core.model.UssdTransaction;
@Repository
public interface TransactionRepository extends JpaRepository<UssdTransaction, Long> {
    
}
