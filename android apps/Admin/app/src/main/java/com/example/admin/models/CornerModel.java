package com.example.admin.models;

public class CornerModel {
    private String cornerId;
    private double lat;
    private double lang;
    private double altitude;

    public CornerModel() {
    }

    public CornerModel(String cornerId, double lat, double lang, double altitude) {
        this.cornerId = cornerId;
        this.lat = lat;
        this.lang = lang;
        this.altitude = altitude;
    }

    public String getCornerId() {
        return cornerId;
    }

    public void setCornerId(String cornerId) {
        this.cornerId = cornerId;
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

    @Override
    public String toString() {
        return "CornerModel{" +
                "cornerId='" + cornerId + '\'' +
                ", lat=" + lat +
                ", lang=" + lang +
                ", altitude=" + altitude +
                '}';
    }
}
