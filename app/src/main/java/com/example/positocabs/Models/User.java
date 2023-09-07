package com.example.positocabs.Models;

public class User {

    public String name,email,gender,dob,userPfp;
    int rating;

    public User(String name, String email, String gender, String dob) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        //this.userPfp = "https://firebasestorage.googleapis.com/v0/b/posito-cabs-fd0bb.appspot.com/o/Users%20and%20drivers%20profile%20pics%2Fdefault_pfp_ico.jpg?alt=media&token=f2af61bf-520a-49b5-8a53-9e2ab6118ed2";
    }

    public User(String name, String email, String gender, String dob, int rating) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.rating = rating;
        //this.userPfp = "https://firebasestorage.googleapis.com/v0/b/posito-cabs-fd0bb.appspot.com/o/Users%20and%20drivers%20profile%20pics%2Fdefault_pfp_ico.jpg?alt=media&token=f2af61bf-520a-49b5-8a53-9e2ab6118ed2";
    }

    public User() {

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

    public String getUserPfp() {
        return userPfp;
    }

    public void setUserPfp(String userPfp) {
        this.userPfp = userPfp;
    }
}
