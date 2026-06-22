package com.fintech.wallet.service;

import com.fintech.wallet.entity.Transaction;
import com.fintech.wallet.entity.Wallet;
import com.fintech.wallet.repository.TransactionRepository;
import com.fintech.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fintech.wallet.dto.TransactionReceipt;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Wallet addFunds(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found!"));

        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setWallet(savedWallet);
        tx.setAmount(amount);
        tx.setTransactionType("DEPOSIT");
        transactionRepository.save(tx);

        return savedWallet;
    }

    // THE NEW TRANSFER ENGINE
    @Transactional
    public String transferFunds(Long senderId, Long receiverId, BigDecimal amount) {
        // 1. Find both wallets
        Wallet sender = walletRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found!"));
        Wallet receiver = walletRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found!"));

        // 2. Security Check: Does the sender have enough money?
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        // 3. Move the money
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        // 4. Save the new balances
        walletRepository.save(sender);
        walletRepository.save(receiver);

        // 5. Create TWO paper trails
        Transaction sendTx = new Transaction();
        sendTx.setWallet(sender);
        sendTx.setAmount(amount);
        sendTx.setTransactionType("TRANSFER_OUT");
        transactionRepository.save(sendTx);

        Transaction receiveTx = new Transaction();
        receiveTx.setWallet(receiver);
        receiveTx.setAmount(amount);
        receiveTx.setTransactionType("TRANSFER_IN");
        transactionRepository.save(receiveTx);

        return "Successfully transferred " + amount + " from Wallet " + senderId + " to Wallet " + receiverId;
    }
    // THE NEW BANK STATEMENT ENGINE
    // THE DTO ENGINE
    public List<TransactionReceipt> getWalletHistory(Long walletId) {
        walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found!"));

        List<Transaction> rawTransactions = transactionRepository.findByWalletId(walletId);

        // Java Streams: A high-end way to loop through data and transform it
        return rawTransactions.stream()
                .map(tx -> new TransactionReceipt(
                        tx.getId(),
                        tx.getTransactionType(),
                        tx.getAmount(),
                        tx.getTimestamp(),
                        tx.getWallet().getId()
                ))
                .toList(); // Packages it back up into a clean list
    }
}