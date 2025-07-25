package com.smartbank.user.service;

import com.smartbank.user.dto.JwtResponse;
import com.smartbank.user.dto.LoginRequest;
import com.smartbank.user.dto.RegisterRequest;
import com.smartbank.user.model.User;
import com.smartbank.user.repository.UserRepository;
import com.smartbank.user.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail().toLowerCase())) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBalance(5000.0);
        user.setRole("USER");
        user.setStatus("ACTIVE");

        userRepo.save(user);
        return ResponseEntity.ok("Registration successful.");
    }

    public ResponseEntity<JwtResponse> loginUser(LoginRequest request) {
        String email = request.getEmail().toLowerCase();

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            System.out.println("❌ User not found for email: " + email);
            return ResponseEntity.status(401).body(null);
        }

        System.out.println("✅ Email found: " + user.getEmail());
        System.out.println("🔐 Encrypted DB password: " + user.getPassword());
        System.out.println("🔑 Raw input password: " + request.getPassword());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ Password mismatch!");
            return ResponseEntity.status(401).body(null);
        }

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(403).body(null);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        JwtResponse response = new JwtResponse(token, user.getRole());

        return ResponseEntity.ok(response);
    }
}
