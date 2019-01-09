package com.example.michelle.driverbuddy;

public class Driver {

    String nic;
    String firstName;
    String lastName;
    int mobile;
    String email;
    int license;
    String token;
    String _id;

    public Driver(String nic, String firstName, String lastName, int mobile, String email, int license, String token, String _id) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.license = license;
        this.token = token;
        this._id = _id;
    }

    public String getObjectId() {
        return _id;
    }

    public void setObjectId(String objectId) {
        this._id = objectId;
    }



    public Driver(String firstName, String lastName, String email, String nic, int license, int mobile) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.license = license;
    }

    public Driver(String firstName, String lastName, String email, String nic, int license, int mobile,String token) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.license = license;
        this.token=token;
    }

    public String getNic() {
        return nic;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public int getLicense() {
        return license;
    }

    public String getToken() {
        return token;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLicense(int license) {
        this.license = license;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

