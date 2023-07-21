package com.example.positocabs.Models;

public class ReadWriteUserDetails {

    public String name, email, gender,dob;
    int rating;

    public ReadWriteUserDetails(String name, String email, String gender, String dob) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
    }

    public ReadWriteUserDetails(String name, String email, String gender, String dob, String phoneNo, int rating) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.phoneNo = phoneNo;
        this.rating = rating;
    }

    public ReadWriteUserDetails() {

    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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
