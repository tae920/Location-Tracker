package com.example.sampleassignment1;

public class UserData {
    public String newAddress = "";
    public String newDate = "";
    public double newLatitude;
    public double newLongitude;

    public UserData() {
    }

    public UserData(String newAddress, String newDate, double newLatitude, double newLongitude) {
        this.newAddress = newAddress;
        this.newDate = newDate;
        this.newLatitude = newLatitude;
        this.newLongitude = newLongitude;
    }

    public double getNewLatitude() {return newLatitude;}

    public void setNewLatitude(double newLatitude) {
        this.newLatitude = newLatitude;
    }

    public double getNewLongitude() {
        return newLongitude;
    }

    public void setNewLongitude(double newLongitude) {this.newLongitude = newLongitude;}


    public String getNewAddress(){return newAddress;}

    public void setNewAddress(String newAddress) {this.newAddress = newAddress;}


    public String getNewDate(){return newDate;}

    public void setNewDate(String newDate) {this.newDate = newDate;}



}