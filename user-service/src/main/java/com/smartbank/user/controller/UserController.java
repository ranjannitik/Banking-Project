package com.smartbank.user.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartbank.user.model.BillPayment;
import com.smartbank.user.model.Card;
import com.smartbank.user.model.Loan;
import com.smartbank.user.model.User;
import com.smartbank.user.repository.BillPaymentRepository;
import com.smartbank.user.repository.CardRepository;
import com.smartbank.user.repository.LoanRepository;
import com.smartbank.user.repository.UserRepository;
import com.smartbank.user.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepo;
    private final CardRepository cardRepo;
    private final LoanRepository loanRepo;
    private final BillPaymentRepository billRepo;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepo, CardRepository cardRepo, LoanRepository loanRepo,
                          BillPaymentRepository billRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.cardRepo = cardRepo;
        this.loanRepo = loanRepo;
        this.billRepo = billRepo;
        this.jwtUtil = jwtUtil;
    }

    // ✅ 1. Get Profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new ProfileResponse(user.getName(), user.getBalance()));
    }

    // ✅ NEW: Check Balance
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

    // ✅ 2. Get All Cards
    @GetMapping("/cards")
    public ResponseEntity<?> getCards(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(cardRepo.findByUser(user));
    }

    // ✅ 3. Add Card
    @PostMapping("/cards")
    public ResponseEntity<?> addCard(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody Card card) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();

        card.setUser(user);
        return ResponseEntity.ok(cardRepo.save(card));
    }

    // ✅ 4. Get Loans
    @GetMapping("/loans")
    public ResponseEntity<?> getLoans(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(loanRepo.findByUser(user));
    }

    // ✅ 5. Add Loan
    @PostMapping("/loans")
    public ResponseEntity<?> addLoan(@RequestHeader("Authorization") String authHeader,
                                     @RequestBody Loan loan) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();

        loan.setUser(user);
        return ResponseEntity.ok(loanRepo.save(loan));
    }

    // ✅ 6. Pay Bill
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

    // ✅ 7. View All Bills
    @GetMapping("/bills")
    public ResponseEntity<?> viewBills(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(billRepo.findByUser(user));
    }

    // ✅ Profile DTO
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

    // ✅ Bill DTO
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
