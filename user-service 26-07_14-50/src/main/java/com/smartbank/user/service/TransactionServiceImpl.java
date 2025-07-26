package com.smartbank.user.service;

import com.smartbank.user.model.Transaction;
import com.smartbank.user.model.User;
import com.smartbank.user.repository.TransactionRepository;
import com.smartbank.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepo;
    private final UserRepository userRepo;

    public TransactionServiceImpl(TransactionRepository transactionRepo, UserRepository userRepo) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        User sender = userRepo.findByEmail(transaction.getSenderEmail())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        if (sender.getBalance() < transaction.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        User receiver = userRepo.findByEmail(transaction.getReceiverEmail())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        receiver.setBalance(receiver.getBalance() + transaction.getAmount());

        userRepo.save(sender);
        userRepo.save(receiver);

        return transactionRepo.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByEmail(String email) {
        return transactionRepo.findBySenderEmailOrReceiverEmail(email, email);
    }
}
