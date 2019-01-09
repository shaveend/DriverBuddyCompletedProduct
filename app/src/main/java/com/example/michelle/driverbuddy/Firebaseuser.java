package com.example.michelle.driverbuddy;

public class Firebaseuser {

    String f_email;
    double lat;
    double lon;

    public Firebaseuser() {
    }

    public Firebaseuser(String f_email, double lat, double lon) {

        this.f_email = f_email;
        this.lat = lat;
        this.lon = lon;
    }

    public String getF_email() {

        return f_email;
    }

    public void setF_email(String f_email) {
        this.f_email = f_email;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }



}
