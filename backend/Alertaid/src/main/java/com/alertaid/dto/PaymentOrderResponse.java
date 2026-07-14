package com.alertaid.dto;

public class PaymentOrderResponse {
    private String provider;
    private String orderId;
    private long amount; // smallest currency unit (paise)
    private String currency;
    private String key;
    private boolean testMode;
    private String message;

    public static PaymentOrderResponse razorpay(String orderId, long amount, String currency, String key, boolean testMode) {
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setProvider("RAZORPAY");
        response.setOrderId(orderId);
        response.setAmount(amount);
        response.setCurrency(currency);
        response.setKey(key);
        response.setTestMode(testMode);
        return response;
    }

    public static PaymentOrderResponse mock(long amount, String currency) {
        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setProvider("MOCK");
        response.setOrderId("order_" + System.currentTimeMillis());
        response.setAmount(amount);
        response.setCurrency(currency);
        response.setTestMode(true);
        response.setMessage("Payment gateway keys not configured. Using mock order for UI testing.");
        response.setKey("rzp_test_placeholder");
        return response;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
