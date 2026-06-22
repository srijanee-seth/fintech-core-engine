package com.fintech.wallet.scheduler;

import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.WalletRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class InterestScheduler {

    private final WalletRepository walletRepository;

    public InterestScheduler(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    // The Cron Expression: "Run every 10 seconds"
    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void calculateAndPayInterest() {
        // 1. Fetch every single wallet in the database
        List<Wallet> allWallets = walletRepository.findAll();
        
        if (allWallets.isEmpty()) {
            return; // If no wallets exist, go back to sleep
        }

        // 2. Define the interest rate (1% = 0.01)
        BigDecimal interestRate = new BigDecimal("0.01");

        // 3. Loop through the list and add the money
        for (Wallet wallet : allWallets) {
            BigDecimal currentBalance = wallet.getBalance();
            BigDecimal interestAmount = currentBalance.multiply(interestRate);
            wallet.setBalance(currentBalance.add(interestAmount));
        }

        // 4. Save all updated wallets back to the database at once
        walletRepository.saveAll(allWallets);
        
        // 5. Print a receipt to the terminal so we can see it working
        System.out.println("🤖 SYSTEM ALERT: Paid 1% interest to " + allWallets.size() + " wallets!");
    }
}