package com.alertaid.controller;

import com.alertaid.model.Donation;
import com.alertaid.security.JwtTokenProvider;
import com.alertaid.service.DonationService;
import com.alertaid.service.PaymentGatewayService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin
public class DonationController {

    private final DonationService donationService;
    private final JwtTokenProvider tokenProvider;
    private final PaymentGatewayService paymentGatewayService;

    public DonationController(DonationService donationService,
                              JwtTokenProvider tokenProvider,
                              PaymentGatewayService paymentGatewayService) {
        this.donationService = donationService;
        this.tokenProvider = tokenProvider;
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/{campaignId}")
    public ResponseEntity<Donation> donate(@PathVariable Long campaignId,
                                           @RequestBody Donation donation,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String email = resolveEmail(authHeader);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        donation.setDonorEmail(email);
        boolean hasGatewayDetails = StringUtils.hasText(donation.getGatewayOrderId())
                && StringUtils.hasText(donation.getGatewayPaymentId())
                && StringUtils.hasText(donation.getGatewaySignature());
        if (hasGatewayDetails) {
            boolean valid = paymentGatewayService.verifySignature(
                    donation.getGatewayOrderId(),
                    donation.getGatewayPaymentId(),
                    donation.getGatewaySignature());
            if (!valid) {
                return ResponseEntity.badRequest().build();
            }
            donation.setPaymentStatus("SUCCESS");
            if (!StringUtils.hasText(donation.getPaymentReference())) {
                donation.setPaymentReference(donation.getGatewayPaymentId());
            }
        } else {
            donation.setPaymentStatus(donation.getPaymentStatus() == null ? "PENDING" : donation.getPaymentStatus());
        }
        donation.setCurrency(donation.getCurrency() == null ? "INR" : donation.getCurrency().toUpperCase());
        donation.setTestMode(paymentGatewayService.isMockMode());
        Donation saved = donationService.donate(campaignId, donation);
        if (saved == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Donation> getDonations() {
        return donationService.getDonations();
    }

    @GetMapping("/me")
    public ResponseEntity<List<Donation>> getMyDonations(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String email = resolveEmail(authHeader);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(donationService.getDonationsByEmail(email));
    }

    private String resolveEmail(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!tokenProvider.validateToken(token)) {
            return null;
        }
        return tokenProvider.getUsernameFromJWT(token);
    }
}
