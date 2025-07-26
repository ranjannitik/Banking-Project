package com.smartbank.user.controller;

import com.smartbank.user.dto.JwtResponse;
import com.smartbank.user.dto.LoginRequest;
import com.smartbank.user.dto.LoginOtpRequest;
import com.smartbank.user.dto.OtpVerifyRequest;
import com.smartbank.user.dto.RegisterRequest;
import com.smartbank.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")  // ✅ Public endpoints
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Registration Step 1: Send OTP
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    // ✅ Registration Step 2: Verify OTP and create account
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {
        RegisterRequest regReq = new RegisterRequest();
        regReq.setName(request.getName());
        regReq.setEmail(request.getEmail());
        regReq.setPassword(request.getPassword());
        return userService.verifyOtpAndRegisterUser(regReq, request.getOtp());
    }

    // ✅ Login Step 1: Validate credentials, send OTP
    @PostMapping("/login")
    public ResponseEntity<?> initiateLogin(@RequestBody LoginRequest request) {
        return userService.sendOtpForLogin(request);
    }

    // ✅ Login Step 2: Verify OTP and return token
    @PostMapping("/login/verify")
    public ResponseEntity<JwtResponse> verifyLoginOtp(@RequestBody LoginOtpRequest request) {
        return userService.verifyLoginOtp(request);
    }
}
