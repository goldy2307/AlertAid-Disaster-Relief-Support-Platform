package com.alertaid.controller;

import com.alertaid.dto.PaymentOrderRequest;
import com.alertaid.dto.PaymentOrderResponse;
import com.alertaid.security.JwtTokenProvider;
import com.alertaid.service.PaymentGatewayService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentGatewayService paymentGatewayService;
    private final JwtTokenProvider tokenProvider;

    public PaymentController(PaymentGatewayService paymentGatewayService, JwtTokenProvider tokenProvider) {
        this.paymentGatewayService = paymentGatewayService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@RequestBody PaymentOrderRequest request,
                                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(401).build();
        }
        PaymentOrderResponse response = paymentGatewayService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    private boolean isAuthorized(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return StringUtils.hasText(token) && tokenProvider.validateToken(token);
    }
}
