package com.software.agricycle;

public class User {

    private String uid;
    private String fullName;
    private String email;
    private String phone;
    private String role;   // "buyer" or "supplier"

    private long star;

    private long numbers;


    // Empty constructor required for Firebase
    public User() {
    }

    public User(String fullName, String email, String phone, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        star=0;
        numbers=0;

    }


    public long getNumbers() {
        return numbers;
    }

    public void setNumbers(long numbers) {
        this.numbers = numbers;
    }

    public long getStar() {
        return star;
    }

    public void setStar(long star) {
        this.star = star;
    }

    // UID
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Full name
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
