package com.example.appsocialactivity.dbmodel;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    GeoPoint location;
    Date date;
    String eventDescription;
    String eventName;
    ArrayList<String> interestsOfEvent;
    Integer numOfPeople;

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ArrayList<String> getInterestsOfEvent() {
        return interestsOfEvent;
    }

    public void setInterestsOfEvent(ArrayList<String> interestsOfEvent) {
        this.interestsOfEvent = interestsOfEvent;
    }

    public Integer getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(Integer numOfPeople) {
        this.numOfPeople = numOfPeople;
    }
}
