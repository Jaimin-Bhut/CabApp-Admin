package com.jb.dev.cabapp_admin.model;


import com.google.firebase.firestore.Exclude;

public class CabModel {
    @Exclude
    private String id;
    private String CabName, CabNumber, CabPerCapacity, CabLaugageCapacity, CabDriver, CabStatus,CabArea;


    public CabModel() {
    }

    public CabModel(String CabName, String CabNumber, String CabPerCapacity, String CabLaugageCapacity, String CabDriver, String CabStatus,String CabArea) {
        this.CabName = CabName;
        this.CabNumber = CabNumber;
        this.CabPerCapacity = CabPerCapacity;
        this.CabLaugageCapacity = CabLaugageCapacity;
        this.CabDriver = CabDriver;
        this.CabStatus = CabStatus;
        this.CabArea=CabArea;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getCabName() {
        return CabName;
    }

    public String getCabNumber() {
        return CabNumber;
    }

    public String getCabPerCapacity() {
        return CabPerCapacity;
    }

    public String getCabLaugageCapacity() {
        return CabLaugageCapacity;
    }

    public String getCabDriver() {
        return CabDriver;
    }

    public String getCabStatus() {
        return CabStatus;
    }

    public String getCabArea() {
        return CabArea;
    }
}
