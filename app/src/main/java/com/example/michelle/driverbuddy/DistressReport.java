package com.example.michelle.driverbuddy;

public class DistressReport {
    String driversNic;
    double myLat;
    double myLon;
    int status;

    public DistressReport() {
    }

    public DistressReport(String driversNic, double myLat, double myLon, int status) {
        this.driversNic = driversNic;
        this.myLat = myLat;
        this.myLon = myLon;
        this.status = status;
    }

    public String getDriversNic() {
        return driversNic;
    }

    public void setDriversNic(String driversNic) {
        this.driversNic = driversNic;
    }

    public double getMyLat() {
        return myLat;
    }

    public void setMyLat(double myLat) {
        this.myLat = myLat;
    }

    public double getMyLon() {
        return myLon;
    }

    public void setMyLon(double myLon) {
        this.myLon = myLon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
