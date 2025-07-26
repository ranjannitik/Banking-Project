package com.smartbank.user.repository;

import com.smartbank.user.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Fetch all transactions where the user was either sender or receiver
    List<Transaction> findBySenderEmailOrReceiverEmail(String senderEmail, String receiverEmail);

    // Optional: Fetch by specific user + order by time desc
    List<Transaction> findBySenderEmailOrReceiverEmailOrderByTimestampDesc(String senderEmail, String receiverEmail);
}
