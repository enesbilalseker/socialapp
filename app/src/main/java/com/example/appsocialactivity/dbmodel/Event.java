package com.example.appsocialactivity.dbmodel;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    GeoPoint location;
    Long date;
    String eventDescription;
    String eventName;
    String interestsOfEvent;
    Integer numOfPeople;
    String nameOfPlace;
    String contactNumber;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }


    public String getNameOfPlace() {
        return nameOfPlace;
    }

    public Event() {

    }

    public Event(GeoPoint location, Long date, String eventDescription, String eventName, String interestsOfEvent, Integer numOfPeople, String nameOfPlace) {
        this.location = location;
        this.date = date;
        this.eventDescription = eventDescription;
        this.eventName = eventName;
        this.interestsOfEvent = interestsOfEvent;
        this.numOfPeople = numOfPeople;
        this.nameOfPlace = nameOfPlace;
    }

    public void setNameOfPlace(String nameOfPlace) {
        this.nameOfPlace = nameOfPlace;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
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

    public String getInterestsOfEvent() {
        return interestsOfEvent;
    }

    public void setInterestsOfEvent(String interestsOfEvent) {
        this.interestsOfEvent = interestsOfEvent;
    }

    public Integer getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(Integer numOfPeople) {
        this.numOfPeople = numOfPeople;
    }
}
