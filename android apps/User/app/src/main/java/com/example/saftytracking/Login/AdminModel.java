package com.example.saftytracking.Login;

import java.io.Serializable;
import java.util.UUID;

public class AdminModel implements Serializable {
    private String adminId;
    private String adminName;
    private String adminEmail;
    private String phoneNumber;
    private double lat;
    private double lang;
    private double altitude;

    public AdminModel() {
        this.adminId = UUID.randomUUID().toString();
    }

    public AdminModel(String adminId, String adminName, String adminEmail, String phoneNumber, double lat, double lang, double altitude) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.phoneNumber = phoneNumber;
        this.lat = lat;
        this.lang = lang;
        this.altitude = altitude;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
