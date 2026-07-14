package com.alertaid.service;

import com.alertaid.dto.PaymentOrderRequest;
import com.alertaid.dto.PaymentOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class PaymentGatewayService {

    private static final Logger log = LoggerFactory.getLogger(PaymentGatewayService.class);

    private final String keyId;
    private final String keySecret;

    public PaymentGatewayService(@Value("${payment.razorpay.keyId:}") String keyId,
                                 @Value("${payment.razorpay.keySecret:}") String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    private boolean gatewayReady() {
        return StringUtils.hasText(keyId) && StringUtils.hasText(keySecret);
    }

    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        long amountRupees = Math.max(request.getAmount(), 0);
        if (amountRupees <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        String currency = StringUtils.hasText(request.getCurrency()) ? request.getCurrency().toUpperCase() : "INR";
        long amountPaise = amountRupees * 100;

        if (!gatewayReady()) {
            return PaymentOrderResponse.mock(amountPaise, currency);
        }

        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", StringUtils.hasText(request.getReceipt()) ? request.getReceipt() : "rcpt_" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1);
            Map<String, String> notes = request.getNotes();
            if (notes != null && !notes.isEmpty()) {
                orderRequest.put("notes", new JSONObject(notes));
            }
            Order order = client.orders.create(orderRequest);
            return PaymentOrderResponse.razorpay(order.get("id"), order.get("amount"), order.get("currency"), keyId, false);
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order", e);
            throw new IllegalStateException("Unable to create payment order. Check gateway configuration.");
        }
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        if (!StringUtils.hasText(orderId) || !StringUtils.hasText(paymentId) || !StringUtils.hasText(signature)) {
            return false;
        }
        if (!gatewayReady()) {
            // Without keys we cannot cryptographically verify; trust the client in mock mode.
            return true;
        }
        try {
            JSONObject data = new JSONObject();
            data.put("razorpay_order_id", orderId);
            data.put("razorpay_payment_id", paymentId);
            data.put("razorpay_signature", signature);
            return Utils.verifyPaymentSignature(data, keySecret);
        } catch (RazorpayException e) {
            log.error("Payment signature verification failed", e);
            return false;
        }
    }

    public boolean isMockMode() {
        return !gatewayReady();
    }

    public String getKeyId() {
        return keyId;
    }
}
