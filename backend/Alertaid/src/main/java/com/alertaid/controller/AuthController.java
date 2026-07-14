package com.alertaid.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alertaid.dto.AuthResponse;
import com.alertaid.dto.LoginRequest;
import com.alertaid.dto.RegisterRequest;
import com.alertaid.model.Role;
import com.alertaid.model.User;
import com.alertaid.security.JwtTokenProvider;
import com.alertaid.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @jakarta.validation.Valid RegisterRequest request) {
        if (userService.getUserByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Role-based required fields
        Role effectiveRole = request.getRole() != null ? request.getRole() : Role.CITIZEN;
        if (effectiveRole == Role.ORG) {
            if (isBlank(request.getOrgName()) || isBlank(request.getOrgType()) || isBlank(request.getLicenseNumber())) {
                return ResponseEntity.badRequest().body("Organization registration requires orgName, orgType, and licenseNumber");
            }
        }
        if (effectiveRole == Role.VOLUNTEER) {
            if (isBlank(request.getExpertise()) || isBlank(request.getAvailability())) {
                return ResponseEntity.badRequest().body("Volunteer registration requires expertise and availability");
            }
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(effectiveRole);

        // Persist all registration fields
        user.setAddress(request.getAddress());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setPincode(request.getPincode());
        user.setGender(request.getGender());

        user.setOrgName(request.getOrgName());
        user.setOrgType(request.getOrgType());
        user.setLicenseNumber(request.getLicenseNumber());
        user.setServices(request.getServices());
        user.setSupportMode(request.getSupportMode());

        user.setExpertise(request.getExpertise());
        user.setAvailability(request.getAvailability());
        user.setExperienceLevel(request.getExperienceLevel());

        // Store the provided password (no default generation), hashed
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userService.saveUser(user);
        return ResponseEntity.created(URI.create("/users/" + saved.getId())).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Accept either email or phone in the "email" field from the client
        String identifier = request.getEmail();
        if (identifier == null || identifier.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Resolve user by email if it looks like an email, otherwise by phone, then fallback to email
        User user = null;
        if (identifier.contains("@")) {
            user = userService.getUserByEmail(identifier).orElse(null);
        } else {
            user = userService.getUserByPhone(identifier).orElse(null);
            if (user == null) {
                user = userService.getUserByEmail(identifier).orElse(null);
            }
        }
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // Verify password explicitly
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        // Enforce role match if client provided a role
        if (request.getRole() != null && user.getRole() != request.getRole()) {
            return ResponseEntity.status(403).build();
        }

        String token = tokenProvider.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        // Legacy stub retained for compatibility
        if (userService.getUserByEmail(email).isEmpty()) {
            return ResponseEntity.badRequest().body("No user found with that email");
        }
        return ResponseEntity.ok("Password reset link sent (stub)");
    }

    @PostMapping("/reset-pin")
    public ResponseEntity<?> resetPin(@RequestBody @jakarta.validation.Valid com.alertaid.dto.ResetPinRequest request) {
        var userOpt = userService.getUserByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No user found with that email");
        }
        var user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getPin()));
        userService.saveUser(user);
        return ResponseEntity.ok().build();
    }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
