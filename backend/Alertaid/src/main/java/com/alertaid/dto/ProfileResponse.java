package com.alertaid.dto;

import java.time.Instant;

import com.alertaid.model.Role;
import com.alertaid.model.User;

public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private String address;
    private String state;
    private String city;
    private String pincode;
    private String gender;
    private String orgName;
    private String orgType;
    private String licenseNumber;
    private String services;
    private String supportMode;
    private String expertise;
    private String availability;
    private String experienceLevel;
    private Instant createdAt;

    public static ProfileResponse from(User user) {
        ProfileResponse resp = new ProfileResponse();
        resp.id = user.getId();
        resp.name = user.getName();
        resp.email = user.getEmail();
        resp.phone = user.getPhone();
        resp.role = user.getRole();
        resp.address = user.getAddress();
        resp.state = user.getState();
        resp.city = user.getCity();
        resp.pincode = user.getPincode();
        resp.gender = user.getGender();
        resp.orgName = user.getOrgName();
        resp.orgType = user.getOrgType();
        resp.licenseNumber = user.getLicenseNumber();
        resp.services = user.getServices();
        resp.supportMode = user.getSupportMode();
        resp.expertise = user.getExpertise();
        resp.availability = user.getAvailability();
        resp.experienceLevel = user.getExperienceLevel();
        resp.createdAt = user.getCreatedAt();
        return resp;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
