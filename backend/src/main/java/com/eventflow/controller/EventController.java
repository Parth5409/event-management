package com.eventflow.controller;

import com.eventflow.model.Event;
import com.eventflow.service.EventService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import spark.Spark;

import java.sql.Date;

public class EventController {
    private final EventService eventService = new EventService();

    // Configure Gson with custom date deserializer
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                try {
                    return Date.valueOf(json.getAsString());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse date: " + json.getAsString(), e);
                }
            })
            .create();

    public void registerRoutes() {
        // Get all events
        Spark.get("/api/events", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(eventService.getAllEvents());
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching events: " + e.getMessage() + "\"}";
            }
        });

        // Get event by ID
        Spark.get("/api/events/:id", (req, res) -> {
            res.type("application/json");
            try {
                int eventId = Integer.parseInt(req.params(":id"));
                Event event = eventService.getEventById(eventId);

                if (event != null) {
                    return gson.toJson(event);
                } else {
                    res.status(404);
                    return "{\"message\":\"Event not found\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid event ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching event: " + e.getMessage() + "\"}";
            }
        });

        // Create a new event
        Spark.post("/api/events", (req, res) -> {
            res.type("application/json");
            try {
                Event newEvent = gson.fromJson(req.body(), Event.class);

                // Validation
                if (newEvent.getTitle() == null || newEvent.getTitle().trim().isEmpty()) {
                    res.status(400);
                    return "{\"message\":\"Event title is required\"}";
                }

                if (newEvent.getEventDate() == null) {
                    res.status(400);
                    return "{\"message\":\"Event date is required\"}";
                }

                Event createdEvent = eventService.createEvent(newEvent);
                res.status(201);
                return gson.toJson(createdEvent);
            } catch (Exception e) {
                res.status(500);
                e.printStackTrace(); // Print full stack trace for debugging
                return "{\"message\":\"Error creating event: " + e.getMessage() + "\"}";
            }
        });

        // Update an event
        Spark.put("/api/events/:id", (req, res) -> {
            res.type("application/json");
            try {
                int eventId = Integer.parseInt(req.params(":id"));
                Event updatedEvent = gson.fromJson(req.body(), Event.class);
                updatedEvent.setEventId(eventId);

                // Validation
                if (updatedEvent.getTitle() == null || updatedEvent.getTitle().trim().isEmpty()) {
                    res.status(400);
                    return "{\"message\":\"Event title is required\"}";
                }

                Event event = eventService.updateEvent(updatedEvent);
                return gson.toJson(event);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid event ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error updating event: " + e.getMessage() + "\"}";
            }
        });

        // Delete an event
        Spark.delete("/api/events/:id", (req, res) -> {
            res.type("application/json");
            try {
                int eventId = Integer.parseInt(req.params(":id"));
                eventService.deleteEvent(eventId);
                return "{\"message\":\"Event deleted successfully\"}";
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid event ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error deleting event: " + e.getMessage() + "\"}";
            }
        });
    }
}
