package com.example.appsocialactivity.dbmodel;


import java.util.ArrayList;

public class User {
    private String nameSurname;
    private String phoneNumber;
    private ArrayList<String> interestList;



    public User( String nameSurname) {
        this.nameSurname = nameSurname;
    }


    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public ArrayList<String> getRecycleBinList() {
        return interestList;
    }

    public void setRecycleBinList(ArrayList<String> recycleBinList) {
        this.interestList = recycleBinList;
    }
}

