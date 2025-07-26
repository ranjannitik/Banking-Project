package com.smartbank.user.service;

import com.smartbank.user.dto.JwtResponse;
import com.smartbank.user.dto.LoginOtpRequest;
import com.smartbank.user.dto.LoginRequest;
import com.smartbank.user.dto.RegisterRequest;
import com.smartbank.user.model.OtpVerification;
import com.smartbank.user.model.User;
import com.smartbank.user.repository.OtpRepository;
import com.smartbank.user.repository.UserRepository;
import com.smartbank.user.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final OtpRepository otpRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserService(UserRepository userRepo, OtpRepository otpRepo,
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.otpRepo = otpRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // STEP 1: Register by sending OTP
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();

        if (userRepo.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email already registered.");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        OtpVerification otpEntry = new OtpVerification(email, otp);
        otpRepo.save(otpEntry);
        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok("OTP sent to email. Please verify to complete registration.");
    }

    // STEP 2: Verify OTP and complete registration
    public ResponseEntity<?> verifyOtpAndRegisterUser(RegisterRequest request, String otpInput) {
        String email = request.getEmail().toLowerCase();

        Optional<OtpVerification> otpData = otpRepo.findById(email);
        if (otpData.isEmpty()) {
            return ResponseEntity.badRequest().body("No OTP found for email.");
        }

        if (!otpData.get().getOtp().equals(otpInput)) {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBalance(5000.0);
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setVerified(true);

        userRepo.save(user);
        otpRepo.deleteById(email);

        return ResponseEntity.ok("✅ Email verified and registration completed.");
    }

    // LOGIN STEP 1: Send OTP
    public ResponseEntity<?> sendOtpForLogin(LoginRequest request) {
        String email = request.getEmail().toLowerCase();
        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        if (user.getVerified() == null || !user.getVerified()) {
            return ResponseEntity.status(403).body("Email not verified");
        }

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(403).body("User is blocked");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpRepo.save(new OtpVerification(email, otp));
        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok("OTP sent to your email");
    }

    // LOGIN STEP 2: Verify OTP and return token
    public ResponseEntity<JwtResponse> verifyLoginOtp(LoginOtpRequest request) {
        String email = request.getEmail().toLowerCase();
        Optional<OtpVerification> otpEntry = otpRepo.findById(email);

        if (otpEntry.isEmpty() || !otpEntry.get().getOtp().equals(request.getOtp())) {
            return ResponseEntity.status(403).body(null);
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(null);
        }

        otpRepo.deleteById(email);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new JwtResponse(token, user.getRole()));
    }
}
