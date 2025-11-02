package com.eventflow.controller;

import com.eventflow.model.Registration;
import com.eventflow.service.RegistrationService;
import com.google.gson.Gson;
import spark.Spark;

public class RegistrationController {
    private final RegistrationService registrationService = new RegistrationService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // Register for an event
        Spark.post("/api/events/:id/register", (req, res) -> {
            res.type("application/json");
            try {
                int eventId = Integer.parseInt(req.params(":id"));
                Registration newRegistration = gson.fromJson(req.body(), Registration.class);

                // Validation
                if (newRegistration.getUserId() <= 0) {
                    res.status(400);
                    return "{\"message\":\"Valid user ID is required\"}";
                }

                newRegistration.setEventId(eventId);
                Registration createdRegistration = registrationService.createRegistration(newRegistration);
                res.status(201);
                return gson.toJson(createdRegistration);
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid event ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error creating registration: " + e.getMessage() + "\"}";
            }
        });

        // Get a user's registrations
        Spark.get("/api/users/:id/registrations", (req, res) -> {
            res.type("application/json");
            try {
                int userId = Integer.parseInt(req.params(":id"));
                return gson.toJson(registrationService.getRegistrationDetailsByUserId(userId));
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid user ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching registrations: " + e.getMessage() + "\"}";
            }
        });

        // Get registrations for an event (admin)
        Spark.get("/api/admin/registrations", (req, res) -> {
            res.type("application/json");
            try {
                String eventIdParam = req.queryParams("eventId");

                if (eventIdParam == null) {
                    res.status(400);
                    return "{\"message\":\"Event ID parameter is required\"}";
                }

                int eventId = Integer.parseInt(eventIdParam);
                return gson.toJson(registrationService.getRegistrationsByEventId(eventId));
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid event ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching registrations: " + e.getMessage() + "\"}";
            }
        });
    }
}
