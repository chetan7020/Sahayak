package com.example.saftytracking.notification;

public class NotificationModel {
    private String name;
    private String mobile;
    private String address;
    private double lat;
    private double lang;
    private double altitude;

    public NotificationModel(String name, String mobile, double lat, double lang, double altitude, String address) {
        this.name = name;
        this.mobile = mobile;
        this.lat = lat;
        this.lang = lang;
        this.altitude = altitude;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLang() {
        return lang;
    }

    public double getAltitude() {
        return altitude;
    }
}
