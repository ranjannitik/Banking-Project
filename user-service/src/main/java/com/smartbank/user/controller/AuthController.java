package com.smartbank.user.controller;

import com.smartbank.user.dto.JwtResponse;
import com.smartbank.user.dto.LoginRequest;
import com.smartbank.user.dto.RegisterRequest;
import com.smartbank.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }
}
