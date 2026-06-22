package com.fintech.wallet.scheduler;

import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.entity.Transaction;
import com.fintech.wallet.repository.WalletRepository;
import com.fintech.wallet.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class InterestScheduler {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository; // Added the ledger!

    // Spring Boot automatically injects both repositories
    public InterestScheduler(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    // The Cron Expression: "Run every 10 seconds"
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void calculateAndPayInterest() {
        System.out.println("🤖 CRON WOKE UP: Attempting to calculate interest...");
        
        try {
            // 1. Fetch every single wallet in the database
            List<Wallet> allWallets = walletRepository.findAll();
            
            if (allWallets.isEmpty()) {
                System.out.println("⚠️ CRON SKIPPED: No wallets found in the database.");
                return; // If no wallets exist, go back to sleep
            }

            // 2. Define the interest rate (1% = 0.01)
            BigDecimal interestRate = new BigDecimal("0.01");

            // 3. Loop through the list and add the money
            for (Wallet wallet : allWallets) {
                BigDecimal currentBalance = wallet.getBalance();
                
                // Do not pay interest if they have 0 or negative money
                if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    continue; 
                }

                BigDecimal interestAmount = currentBalance.multiply(interestRate);
                wallet.setBalance(currentBalance.add(interestAmount));
                
                // 4. CRITICAL FIX: Create a ledger record so Streamlit can see it!
                Transaction interestLedger = new Transaction();
                interestLedger.setAmount(interestAmount);
                
                // Changed from setType() to setTransactionType() to fix the compilation symbol error.
                // If this still fails, your Transaction entity might use an Enum (e.g., TransactionType.DEPOSIT)
                interestLedger.setTransactionType("DEPOSIT"); 
                
                interestLedger.setTimestamp(LocalDateTime.now());
                interestLedger.setWallet(wallet);
                
                // Save the receipt to the database
                transactionRepository.save(interestLedger);
            }

            // 5. Save all updated wallets back to the database
            walletRepository.saveAll(allWallets);
            
            System.out.println("✅ CRON SUCCESS: Paid 1% interest to " + allWallets.size() + " wallets and updated the ledger!");
            
        } catch (Exception e) {
            System.out.println("❌ CRON FAILED: " + e.getMessage());
        }
    }
}