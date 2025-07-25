package com.smartbank.user.repository;

import com.smartbank.user.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderEmailOrReceiverEmail(String senderEmail, String receiverEmail);
}
