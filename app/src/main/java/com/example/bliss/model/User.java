package com.example.bliss.model;

public class User {
    private String email;
    private String fullName;
    private String phone;

    public User(String email, String fullName, String phoneNumber) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phoneNumber;
    }

    public User() {
    }

    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + fullName + '\'' +
                ", phoneNumber='" + phone + '\'' +
                '}';
    }
}
