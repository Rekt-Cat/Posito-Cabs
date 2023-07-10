package com.example.positocabs.Models;

public class ReadWriteUserDetails {

    public String phoneNo, name, email;

    public ReadWriteUserDetails(){

    }
    public ReadWriteUserDetails(String phoneNo, String name, String email) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
