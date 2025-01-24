package com.example.admin.models;

import java.io.Serializable;
import java.util.UUID;

public class UserModel implements Serializable {
    private String userId;
    private String userName;
    private String userEmail;
    private String phoneNumber;
    private double lat;
    private double lang;
    private double altitude;
    private String adminEmail;
    private boolean isVolunteer;
    private int callFlag;
    private int emergencyFlag;
    private double homeLat;
    private double homeLang;


    public UserModel() {
        this.userId = UUID.randomUUID().toString();
        this.callFlag = 0;
        this.emergencyFlag = 0;
    }

    public UserModel(String userId, String userName, String userEmail, String phoneNumber, double lat, double lang, double altitude, String adminEmail, boolean isVolunteer, int callFlag, int emergencyFlag, double homeLat, double homeLang) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.phoneNumber = phoneNumber;
        this.lat = lat;
        this.lang = lang;
        this.altitude = altitude;
        this.adminEmail = adminEmail;
        this.isVolunteer = isVolunteer;
        this.callFlag = callFlag;
        this.emergencyFlag = emergencyFlag;
        this.homeLat = homeLat;
        this.homeLang = homeLang;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public void setVolunteer(boolean volunteer) {
        isVolunteer = volunteer;
    }

    public int getCallFlag() {
        return callFlag;
    }

    public void setCallFlag(int callFlag) {
        this.callFlag = callFlag;
    }

    public int getEmergencyFlag() {
        return emergencyFlag;
    }

    public void setEmergencyFlag(int emergencyFlag) {
        this.emergencyFlag = emergencyFlag;
    }

    public double getHomeLat() {
        return homeLat;
    }

    public void setHomeLat(double homeLat) {
        this.homeLat = homeLat;
    }

    public double getHomeLang() {
        return homeLang;
    }

    public void setHomeLang(double homeLang) {
        this.homeLang = homeLang;
    }
}

