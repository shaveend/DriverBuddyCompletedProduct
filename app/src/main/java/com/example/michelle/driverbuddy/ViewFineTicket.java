package com.example.michelle.driverbuddy;

public class ViewFineTicket {

    String offense;
    String amount;
    String officer;
    String timestamp;
    String driver;
    boolean paid;

    public ViewFineTicket(String offense, String amount, String officer, String timestamp,boolean paid) {
        this.offense = offense;
        this.amount = amount;
        this.officer = officer;
        this.timestamp = timestamp;
        this.paid=paid;
    }




    public ViewFineTicket(String offense, String amount, String officer, String timestamp) {
        this.offense = offense;
        this.amount = amount;
        this.officer = officer;
        this.timestamp = timestamp;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getOffense() {
        return offense;
    }

    public void setOffense(String offense) {
        this.offense = offense;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
