package com.example.michelle.driverbuddy;

public class Insurance {

    String nic;
    String firstName;
    String lastName;
    int mobile;
    String email;
    String agentId;
    String token;

    public Insurance(String nic, String firstName, String lastName, int mobile, String email, String agentId) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.agentId = agentId;
    }


    public Insurance(String nic, String firstName, String lastName, int mobile, String email, String agentId,String token) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.agentId = agentId;
        this.token=token;
    }
    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
