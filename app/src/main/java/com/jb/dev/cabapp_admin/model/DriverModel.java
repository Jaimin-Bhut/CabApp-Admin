package com.jb.dev.cabapp_admin.model;

import com.google.firebase.firestore.Exclude;

public class DriverModel {

    private String Name, Address, Email, Phone, Password;
    @Exclude
    private String id;

    public DriverModel() {
    }

    public DriverModel(String name, String address, String email, String phone, String password) {
        this.Name = name;
        this.Address = address;
        this.Email = email;
        this.Phone = phone;
        this.Password = password;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }
}
