package com.example.michelle.driverbuddy;

public class SpotFine {

    String fine_id;
    String name;
    String amount;
    String description;


    public SpotFine(String fine_id, String name, String amount, String description) {
        this.fine_id = fine_id;
        this.name = name;
        this.amount = amount;
        this.description = description;
    }

    public String getFine_id() {
        return fine_id;
    }

    public void setFine_id(String fine_id) {
        this.fine_id = fine_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
