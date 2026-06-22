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
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.WalletRepository;

@RestController
@RequestMapping("/api/wallets")
@Validated 
public class WalletController {

    private final WalletService walletService;
    private final WalletRepository walletRepository; // <--- 1. We added the database repository

    // <--- 2. We updated the constructor to include it
    public WalletController(WalletService walletService, WalletRepository walletRepository) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    // ==========================================
    // 🚀 THE MISSING ENDPOINTS FOR STREAMLIT!
    // ==========================================

    // 3. The door for fetching all wallets for the dashboard table
    @GetMapping
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    // 4. The door for creating a new wallet from the sidebar
    @PostMapping
    public Wallet createWallet(@RequestBody Wallet newWallet) {
        return walletRepository.save(newWallet);
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