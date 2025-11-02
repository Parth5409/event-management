package com.eventflow.controller;

import com.eventflow.model.Venue;
import com.eventflow.service.VenueService;
import com.google.gson.Gson;
import spark.Spark;

public class VenueController {
    private final VenueService venueService = new VenueService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // Get all venues for the logged-in organizer
        Spark.get("/api/venues", (req, res) -> {
            res.type("application/json");
            try {
                int organizerId = req.attribute("userId");
                return gson.toJson(venueService.getVenuesByOrganizerId(organizerId));
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching venues: " + e.getMessage() + "\"}";
            }
        });

        // Create a new venue
        Spark.post("/api/venues", (req, res) -> {
            res.type("application/json");
            try {
                int organizerId = req.attribute("userId");
                Venue newVenue = gson.fromJson(req.body(), Venue.class);

                if (newVenue.getName() == null || newVenue.getName().trim().isEmpty()) {
                    res.status(400);
                    return "{\"message\":\"Venue name is required\"}";
                }

                Venue createdVenue = venueService.createVenue(newVenue, organizerId);
                res.status(201);
                return gson.toJson(createdVenue);
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error creating venue: " + e.getMessage() + "\"}";
            }
        });
    }
}
