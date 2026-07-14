package com.alertaid.dto;

public class ProfileUpdateRequest {
    private String name;
    private String phone;
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

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
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
