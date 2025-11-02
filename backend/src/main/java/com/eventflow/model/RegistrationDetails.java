package com.eventflow.model;

import java.sql.Date;
import java.sql.Timestamp;

public class RegistrationDetails {

    // From Registration
    private int regId;
    private Timestamp registeredAt;

    // From Event
    private int eventId;
    private String title;
    private String description;
    private Date eventDate;

    // From User
    private int userId;
    private String fullName;
    private String email;

    // Getters and Setters
    public int getRegId() { return regId; }
    public void setRegId(int regId) { this.regId = regId; }

    public Timestamp getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Timestamp registeredAt) { this.registeredAt = registeredAt; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
