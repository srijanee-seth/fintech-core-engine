package com.fintech.wallet.controller;

import com.fintech.wallet.entity.Transaction;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.service.WalletService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.fintech.wallet.dto.TransactionReceipt;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@Validated // THIS ACTIVATES THE FIREWALL FOR THE WHOLE CLASS
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PutMapping("/{id}/deposit")
    public Wallet depositFunds(
            @PathVariable Long id, 
            // FIREWALL: Cannot be blank, and MUST be greater than zero
            @RequestParam @NotNull @Positive(message = "Deposit amount must be strictly greater than zero") BigDecimal amount) {
        return walletService.addFunds(id, amount);
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam Long senderId, 
            @RequestParam Long receiverId, 
            // FIREWALL: Cannot transfer negative money
            @RequestParam @NotNull @Positive(message = "Transfer amount must be strictly greater than zero") BigDecimal amount) {
        return walletService.transferFunds(senderId, receiverId, amount);
    }

   @GetMapping("/{id}/history")
    public List<TransactionReceipt> getHistory(@PathVariable Long id) {
        return walletService.getWalletHistory(id);
    }
}