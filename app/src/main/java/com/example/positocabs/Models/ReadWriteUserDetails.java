package com.example.positocabs.Models;

public class ReadWriteUserDetails {

    public String email,password,id;

    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails( String email, String password,String id) {
        this.email = email;
        this.password = password;
        this.id=id;
    }
}
