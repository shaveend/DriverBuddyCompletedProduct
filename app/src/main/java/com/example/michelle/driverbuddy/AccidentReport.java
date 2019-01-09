package com.example.michelle.driverbuddy;

import java.util.Date;

public class AccidentReport {

    String vehicleNo;
    Driver driver[];
    Insurance insurance[];
    String place;
    String description;
    Date timeStamp;
    String nic;
    String agentId;
    String insuranceNumber;
    int month;
    int year;

    public AccidentReport(String vehicleNo,String place, String description,String insuranceNumber,Driver[] driver,Insurance[] insurance,Date timeStamp) {
        this.vehicleNo = vehicleNo;
        this.driver = driver;
        this.insurance = insurance;
        this.place = place;
        this.description = description;
        this.insuranceNumber=insuranceNumber;
        this.timeStamp=timeStamp;
    }

    public AccidentReport(String vehicleNo, String place, String description,String insuranceNumber, String nic, String agentId) {
        this.vehicleNo = vehicleNo;
        this.place = place;
        this.description = description;
        this.insuranceNumber=insuranceNumber;
        this.nic = nic;
        this.agentId = agentId;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Driver[] getDriver() {
        return driver;
    }

    public void setDriver(Driver[] driver) {
        this.driver = driver;
    }

    public Insurance[] getInsurance() {
        return insurance;
    }

    public void setInsurance(Insurance[] insurance) {
        this.insurance = insurance;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}