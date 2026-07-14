package com.alertaid.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alertaid.dto.ProfileResponse;
import com.alertaid.dto.ProfileUpdateRequest;
import com.alertaid.model.User;
import com.alertaid.service.UserService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService
            .getUserByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(ProfileResponse.from(user));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(Authentication authentication,
                                                         @RequestBody ProfileUpdateRequest request) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService
            .getUserByEmail(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getPincode() != null) user.setPincode(request.getPincode());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getOrgName() != null) user.setOrgName(request.getOrgName());
        if (request.getOrgType() != null) user.setOrgType(request.getOrgType());
        if (request.getLicenseNumber() != null) user.setLicenseNumber(request.getLicenseNumber());
        if (request.getServices() != null) user.setServices(request.getServices());
        if (request.getSupportMode() != null) user.setSupportMode(request.getSupportMode());
        if (request.getExpertise() != null) user.setExpertise(request.getExpertise());
        if (request.getAvailability() != null) user.setAvailability(request.getAvailability());
        if (request.getExperienceLevel() != null) user.setExperienceLevel(request.getExperienceLevel());

        User saved = userService.saveUser(user);
        return ResponseEntity.ok(ProfileResponse.from(saved));
    }
}
