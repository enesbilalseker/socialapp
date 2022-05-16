package com.example.appsocialactivity.dbmodel;


import java.util.ArrayList;

public class User {
    private String nameSurname;
    private String phoneNumber;
    private ArrayList<String> interestsOfUser;
    private ArrayList<Event> eventList;

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    public User(String nameSurname) {
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

    public ArrayList<String> getInterestsOfUser() {
        return interestsOfUser;
    }

    public void setInterestsOfUser(ArrayList<String> interestsOfUser) {
        this.interestsOfUser = interestsOfUser;
    }
    public User(){}

}

