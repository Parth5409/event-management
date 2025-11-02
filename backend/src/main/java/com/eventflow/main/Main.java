package com.eventflow.main;

import com.eventflow.controller.EventController;
import com.eventflow.controller.RegistrationController;
import com.eventflow.controller.UserController;
import com.eventflow.controller.VenueController;
import com.eventflow.controller.OrganizerController;
import com.eventflow.util.AuthMiddleware;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        // Set port
        Spark.port(8080);

        // IMPORTANT: Handle OPTIONS requests BEFORE other routes
        Spark.options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        // Enable CORS for all routes
        Spark.after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:5173");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        // Initialize controllers
        UserController userController = new UserController();
        EventController eventController = new EventController();
        RegistrationController registrationController = new RegistrationController();
        VenueController venueController = new VenueController();
        OrganizerController organizerController = new OrganizerController();

        // Register routes
        userController.registerRoutes();
        eventController.registerRoutes();
        registrationController.registerRoutes();
        venueController.registerRoutes();
        organizerController.registerRoutes();

        // Enable auth middleware
        AuthMiddleware.enableAuthMiddleware();

        System.out.println("Server is running on port 8080");
    }
}
