package com.fintech.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The amount being moved
    private BigDecimal amount;

    // e.g., "DEPOSIT", "WITHDRAWAL", "TRANSFER"
    private String transactionType;

    // Automatically records exactly when this happened
    private LocalDateTime timestamp = LocalDateTime.now();

    // This links Many Transactions to One Wallet
    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;
}