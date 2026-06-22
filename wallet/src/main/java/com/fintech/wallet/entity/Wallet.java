package com.fintech.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We use BigDecimal for money because standard 'double' math can lose pennies!
    private BigDecimal balance = BigDecimal.ZERO;

    // This links exactly one Wallet to exactly one User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}