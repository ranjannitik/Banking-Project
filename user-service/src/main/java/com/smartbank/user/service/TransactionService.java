package com.smartbank.user.service;

import com.smartbank.user.model.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction processTransaction(Transaction transaction);
    List<Transaction> getTransactionsByEmail(String email);
}
