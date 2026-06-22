package com.fintech.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// A "record" is a modern Java feature. It automatically creates an immutable 
// data carrier without needing @Data or standard class boilerplate!
public record TransactionReceipt(
        Long transactionId,
        String type,
        BigDecimal amount,
        LocalDateTime timestamp,
        Long walletId
) {
}