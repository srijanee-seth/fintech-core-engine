package com.fintech.wallet.repository;

import com.fintech.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Spring Boot magic: It reads the English words "findByWalletId" 
    // and automatically writes the exact SQL query for you!
    List<Transaction> findByWalletId(Long walletId);
    
}