package com.example.admin.models;

public class NotificationModel {
    private String name;
    private String phoneNumber;
    private double lat;
    private double lang;
    private double alti;

    public NotificationModel() {
    }

    public NotificationModel(String name, String phoneNumber, double lat, double lang, double alti) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.lat = lat;
        this.lang = lang;
        this.alti = alti;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getAlti() {
        return alti;
    }

    public void setAlti(double alti) {
        this.alti = alti;
    }
}
