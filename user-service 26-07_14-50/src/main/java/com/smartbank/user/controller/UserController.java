package com.smartbank.user.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartbank.user.dto.TransferRequest;
import com.smartbank.user.model.*;
import com.smartbank.user.repository.*;
import com.smartbank.user.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user") // 🔐 Protected routes (require token)
public class UserController {

    private final UserRepository userRepo;
    private final CardRepository cardRepo;
    private final LoanRepository loanRepo;
    private final BillPaymentRepository billRepo;
    private final TransactionRepository txRepo;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepo, CardRepository cardRepo, LoanRepository loanRepo,
                          BillPaymentRepository billRepo, TransactionRepository txRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
        this.loanRepo = loanRepo;
        this.billRepo = billRepo;
        this.txRepo = txRepo;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ProfileResponse(user.getName(), user.getBalance()));
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "balance", user.getBalance()
        ));
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getCards(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(cardRepo.findByUser(user));
    }

    @PostMapping("/cards")
    public ResponseEntity<?> addCard(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody Card card) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();

        card.setUser(user);
        return ResponseEntity.ok(cardRepo.save(card));
    }

    @GetMapping("/loans")
    public ResponseEntity<?> getLoans(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(loanRepo.findByUser(user));
    }

    @PostMapping("/loans")
    public ResponseEntity<?> addLoan(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody Loan loan) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();

        loan.setUser(user);
        return ResponseEntity.ok(loanRepo.save(loan));
    }

    @PostMapping("/pay-bill")
    public ResponseEntity<?> payBill(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody BillPaymentRequest bill) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null || user.getBalance() < bill.getAmount()) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        user.setBalance(user.getBalance() - bill.getAmount());
        userRepo.save(user);

        BillPayment billPayment = new BillPayment();
        billPayment.setUser(user);
        billPayment.setBillType(bill.getBillType());
        billPayment.setBillNumber(bill.getBillNumber());
        billPayment.setAmount(bill.getAmount());
        billRepo.save(billPayment);

        return ResponseEntity.ok("Bill payment successful");
    }

    @GetMapping("/bills")
    public ResponseEntity<?> viewBills(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(billRepo.findByUser(user));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody TransferRequest req) {
        String senderEmail = jwtUtil.extractEmail(authHeader.substring(7));
        User sender = userRepo.findByEmail(senderEmail).orElse(null);
        User receiver = userRepo.findByEmail(req.getReceiverEmail()).orElse(null);

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body("Invalid sender or receiver email.");
        }

        if (sender.getBalance() < req.getAmount()) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        // Transfer
        sender.setBalance(sender.getBalance() - req.getAmount());
        receiver.setBalance(receiver.getBalance() + req.getAmount());
        userRepo.save(sender);
        userRepo.save(receiver);

        // Log transaction
        Transaction tx = new Transaction();
        tx.setSenderEmail(senderEmail);
        tx.setReceiverEmail(req.getReceiverEmail());
        tx.setAmount(req.getAmount());
        tx.setType("TRANSFER");
        tx.setTimestamp(LocalDateTime.now());
        txRepo.save(tx);

        return ResponseEntity.ok("Money transferred successfully");
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getUserTransactions(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        List<Transaction> txList = txRepo.findBySenderEmailOrReceiverEmail(email, email);
        return ResponseEntity.ok(txList);
    }

    public static class ProfileResponse {
        private String name;
        private double balance;

        public ProfileResponse(String name, double balance) {
            this.name = name;
            this.balance = balance;
        }

        public String getName() { return name; }
        public double getBalance() { return balance; }
    }

    public static class BillPaymentRequest {
        @JsonProperty("billType")
        private String billType;
        @JsonProperty("billNumber")
        private String billNumber;
        @JsonProperty("amount")
        private double amount;

        public String getBillType() { return billType; }
        public void setBillType(String billType) { this.billType = billType; }

        public String getBillNumber() { return billNumber; }
        public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }
}
