package com.eventflow.controller;

import com.eventflow.service.EventService;
import com.eventflow.service.RegistrationService;
import com.google.gson.Gson;
import spark.Spark;

public class OrganizerController {
    private final EventService eventService = new EventService();
    private final RegistrationService registrationService = new RegistrationService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // Get all events for the logged-in organizer
        Spark.get("/api/organizer/events", (req, res) -> {
            res.type("application/json");
            try {
                int organizerId = req.attribute("userId");
                return gson.toJson(eventService.getEventsByOrganizerId(organizerId));
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching organizer events: " + e.getMessage() + "\"}";
            }
        });

        // Get all registrations for a specific event owned by the organizer
        Spark.get("/api/organizer/events/:id/registrations", (req, res) -> {
            res.type("application/json");
            try {
                int organizerId = req.attribute("userId");
                int eventId = Integer.parseInt(req.params(":id"));

                // Security Check: Ensure the event belongs to the organizer
                boolean isOwner = eventService.isEventOwner(organizerId, eventId);
                if (!isOwner) {
                    res.status(403);
                    return "{\"message\":\"Forbidden: You do not own this event.\"}";
                }

                return gson.toJson(registrationService.getRegistrationDetailsByEventId(eventId));
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid event ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching event registrations: " + e.getMessage() + "\"}";
            }
        });
    }
}
