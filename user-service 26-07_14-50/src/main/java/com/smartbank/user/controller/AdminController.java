package com.smartbank.user.controller;

import com.smartbank.user.model.LogEntry;
import com.smartbank.user.model.User;
import com.smartbank.user.repository.BillPaymentRepository;
import com.smartbank.user.repository.CardRepository;
import com.smartbank.user.repository.LoanRepository;
import com.smartbank.user.repository.LogRepository;
import com.smartbank.user.repository.UserRepository;
import jakarta.transaction.Transactional; // ✅ Import added
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepo;
    private final LogRepository logRepo;
    private final CardRepository cardRepo;
    private final LoanRepository loanRepo;
    private final BillPaymentRepository billRepo;

    public AdminController(UserRepository userRepo, LogRepository logRepo,
                           CardRepository cardRepo, LoanRepository loanRepo, BillPaymentRepository billRepo) {
        this.userRepo = userRepo;
        this.logRepo = logRepo;
        this.cardRepo = cardRepo;
        this.loanRepo = loanRepo;
        this.billRepo = billRepo;
    }

    // ✅ 1. Get All Users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    // ✅ 2. Block user by email
    @PutMapping("/block/{email}")
    public ResponseEntity<String> blockUser(@PathVariable String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        user.setStatus("BLOCKED");
        userRepo.save(user);
        return ResponseEntity.ok("User blocked successfully.");
    }

    // ✅ 3. Unblock user by email
    @PutMapping("/unblock/{email}")
    public ResponseEntity<String> unblockUser(@PathVariable String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        user.setStatus("ACTIVE");
        userRepo.save(user);
        return ResponseEntity.ok("User unblocked successfully.");
    }

    // ✅ 4. Delete user and their related data by email
    @DeleteMapping("/delete/{email}")
    @Transactional // ✅ Required to manage delete cascade manually
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        // Delete related data
        cardRepo.deleteAllByUser(user);
        loanRepo.deleteAllByUser(user);
        billRepo.deleteAllByUser(user);

        // Delete the user
        userRepo.delete(user);
        return ResponseEntity.ok("User and related data deleted successfully.");
    }

    // ✅ 5. View logs
    @GetMapping("/logs")
    public ResponseEntity<List<LogEntry>> getLogs() {
        return ResponseEntity.ok(logRepo.findAll());
    }
}
