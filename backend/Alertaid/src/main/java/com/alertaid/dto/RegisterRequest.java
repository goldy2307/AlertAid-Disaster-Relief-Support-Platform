package com.alertaid.dto;

import com.alertaid.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 4, max = 255)
    private String password;

    @Size(max = 15)
    private String phone;

    private Role role;

    // Extended fields from registration form
    @Size(max = 255)
    private String address;
    @Size(max = 100)
    private String state;
    @Size(max = 100)
    private String city;
    @Size(max = 10)
    private String pincode;
    @Size(max = 20)
    private String gender;

    // Organization-specific fields
    @Size(max = 200)
    private String orgName;
    @Size(max = 100)
    private String orgType;
    @Size(max = 100)
    private String licenseNumber;
    @Size(max = 255)
    private String services;
    @Size(max = 50)
    private String supportMode;

    // Volunteer-specific fields
    @Size(max = 255)
    private String expertise;
    @Size(max = 50)
    private String availability;
    @Size(max = 50)
    private String experienceLevel;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }
    public String getSupportMode() { return supportMode; }
    public void setSupportMode(String supportMode) { this.supportMode = supportMode; }

    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
}
