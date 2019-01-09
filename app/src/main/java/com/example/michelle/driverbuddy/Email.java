package com.example.michelle.driverbuddy;

public class Email {
    String [] accepted;
    int envelopeTime;
    //String from_1;

    public Email( String[] accepted, int envelopeTime){
        this.accepted = accepted;
        this.envelopeTime = envelopeTime;
        //this.from_1 = from_1;
    }

    public String [] getAccepted(){
        return accepted;
    }

    public void setAccepted(String[] accepted){
        this.accepted = accepted;
    }

    public int getEnvelopeTime(){
        return envelopeTime;
    }

    public void setEnvelopeTime(int envelopeTime){
        this.envelopeTime = envelopeTime;
    }

    /*public String getFrom_1(){
        return from_1;
    }

    public void setFrom_1(){
        this.from_1 = from_1;
    };*/
}
