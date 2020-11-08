package com.cool.smartappointmentorganizer.model;

public class Appointment {
    public String id;
    public String title;
    public String contactName;
    public String contactNumber;
    public String date;
    public String startTime;
    public String endTime;
    public String note;
    public String tags;
    public String remainderBefore;
    public String location;
    public String createdOn;
    public String updatedOn;

    public Appointment() {
    }

    public Appointment(String id, String title, String contactName, String contactNumber, String date, String startTime, String endTime, String note, String tags, String remainderBefore, String location, String createdOn, String updatedOn) {
        this.id = id;
        this.title = title;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.note = note;
        this.tags = tags;
        this.remainderBefore = remainderBefore;
        this.location = location;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRemainderBefore() {
        return remainderBefore;
    }

    public void setRemainderBefore(String remainderBefore) {
        this.remainderBefore = remainderBefore;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }
}
