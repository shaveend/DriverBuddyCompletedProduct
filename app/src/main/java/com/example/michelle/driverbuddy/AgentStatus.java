package com.example.michelle.driverbuddy;

public class AgentStatus {
    int status;
    String nic;

    public AgentStatus() {
    }

    public AgentStatus(int status, String nic) {
        this.status = status;
        this.nic = nic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getnic() {
        return nic;
    }

    public void setNIC(String nic) {
        this.nic = nic;
    }
}
