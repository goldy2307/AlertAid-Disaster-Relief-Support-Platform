package com.alertaid.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPinRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "PIN must be 6 digits")
    private String pin;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}