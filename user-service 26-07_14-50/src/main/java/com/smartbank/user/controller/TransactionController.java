package com.smartbank.user.controller;


import com.smartbank.user.model.Transaction;
import com.smartbank.user.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // 💸 Send Money
    @PostMapping
    public ResponseEntity<?> transferMoney(@RequestBody Transaction transaction, Principal principal) {
        String senderEmail = principal.getName();

        // override to prevent misuse
        transaction.setSenderEmail(senderEmail);

        try {
            Transaction processed = transactionService.processTransaction(transaction);
            return ResponseEntity.ok(processed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 📜 View Transactions (Sent/Received)
    @GetMapping
    public ResponseEntity<List<Transaction>> getUserTransactions(Principal principal) {
        return ResponseEntity.ok(transactionService.getTransactionsByEmail(principal.getName()));
    }
}
