package com.example.positocabs.Models;

public class ReadWriteUserDetails {

    public String name, email, gender,dob;
    String phoneNo;

    public ReadWriteUserDetails(String name, String phoneNo, String email, String gender, String dob) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
    }

    public ReadWriteUserDetails() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
