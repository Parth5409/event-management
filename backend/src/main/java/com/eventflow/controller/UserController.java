package com.eventflow.controller;

import com.eventflow.model.User;
import com.eventflow.service.UserService;
import com.google.gson.Gson;
import spark.Spark;

public class UserController {
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // Register a new user
        Spark.post("/api/auth/register", (req, res) -> {
            res.type("application/json");

            try {
                User newUser = gson.fromJson(req.body(), User.class);

                // Validation
                if (newUser.getEmail() == null || !newUser.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    res.status(400);
                    return "{\"message\":\"Invalid email format\"}";
                }

                if (newUser.getPassword() == null || newUser.getPassword().length() < 8) {
                    res.status(400);
                    return "{\"message\":\"Password must be at least 8 characters\"}";
                }

                if (newUser.getFullName() == null || newUser.getFullName().trim().isEmpty()) {
                    res.status(400);
                    return "{\"message\":\"Full name is required\"}";
                }

                User createdUser = userService.registerUser(newUser);
                createdUser.setPassword(null); // SECURITY: Don't return password
                res.status(201);
                return gson.toJson(createdUser);
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Registration failed: " + e.getMessage() + "\"}";
            }
        });

        // Login a user
        Spark.post("/api/auth/login", (req, res) -> {
            res.type("application/json");

            try {
                User loginAttempt = gson.fromJson(req.body(), User.class);

                if (loginAttempt.getEmail() == null || loginAttempt.getPassword() == null) {
                    res.status(400);
                    return "{\"message\":\"Email and password are required\"}";
                }

                String token = userService.loginUser(loginAttempt.getEmail(), loginAttempt.getPassword());

                if (token != null) {
                    return "{\"token\":\"" + token + "\"}";
                } else {
                    res.status(401);
                    return "{\"message\":\"Invalid credentials\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Login failed: " + e.getMessage() + "\"}";
            }
        });

        // Get user by ID
        Spark.get("/api/users/:id", (req, res) -> {
            res.type("application/json");

            try {
                int userId = Integer.parseInt(req.params(":id"));
                User user = userService.getUserById(userId);

                if (user != null) {
                    user.setPassword(null); // SECURITY: Don't return password
                    return gson.toJson(user);
                } else {
                    res.status(404);
                    return "{\"message\":\"User not found\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"message\":\"Invalid user ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"message\":\"Error fetching user: " + e.getMessage() + "\"}";
            }
        });
    }
}
