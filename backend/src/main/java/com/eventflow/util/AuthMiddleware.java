package com.eventflow.util;

import com.eventflow.service.AuthService;
import io.jsonwebtoken.Claims;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Arrays;
import java.util.List;

public class AuthMiddleware {

    private static AuthService authService = new AuthService();

    public static void enableAuthMiddleware() {
        Spark.before((req, res) -> {
            if (isProtectedRoute(req)) {
                String token = req.headers("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                    Spark.halt(401, "{\"message\":\"Unauthorized\"}");
                }
                token = token.substring(7);

                if (!authService.validateToken(token)) {
                    Spark.halt(401, "{\"message\":\"Unauthorized\"}");
                }

                Claims claims = authService.getClaimsFromToken(token);
                String role = claims.get("role", String.class);
                req.attribute("userRole", role);
                req.attribute("userId", Integer.parseInt(claims.getSubject()));

                if (!hasPermission(req, role)) {
                    Spark.halt(403, "{\"message\":\"Forbidden\"}");
                }
            }
        });
    }

    private static boolean isProtectedRoute(Request req) {
        String path = req.pathInfo();
        String method = req.requestMethod();

        // Do not protect OPTIONS pre-flight requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return false;
        }

        // All POST, PUT, DELETE are protected (except auth routes)
        if (Arrays.asList("POST", "PUT", "DELETE").contains(method) && !path.startsWith("/api/auth")) {
            return true;
        }

        // Specific GET routes that are protected
        if ("GET".equalsIgnoreCase(method)) {
                                    List<String> protectedGetRoutes = Arrays.asList(
                                            "/api/users/.*/registrations",
                                            "/api/admin/registrations",
                                            "/api/venues",
                                            "/api/organizer/events",
                                            "/api/organizer/events/.*/registrations"
                                    );            return protectedGetRoutes.stream().anyMatch(path::matches);
        }

        return false;
    }

    private static boolean hasPermission(Request req, String role) {
        String path = req.pathInfo();
        String method = req.requestMethod();

        if (path.equals("/api/events") && method.equals("POST")) {
            return "organizer".equals(role) || "admin".equals(role);
        }
        if (path.matches("/api/events/.*") && (method.equals("PUT") || method.equals("DELETE"))) {
            return "organizer".equals(role) || "admin".equals(role);
        }
        if (path.matches("/api/events/.*/register") && method.equals("POST")) {
            return "attendee".equals(role);
        }
        if (path.matches("/api/users/.*/registrations") && method.equals("GET")) {
            // User can access their own registrations, or admin can access any.
            try {
                int userIdFromToken = req.attribute("userId");
                String[] parts = path.split("/");
                int userIdFromPath = Integer.parseInt(parts[3]); // Assumes path is /api/users/{id}/registrations
                return userIdFromToken == userIdFromPath || "admin".equals(role);
            } catch (Exception e) {
                return false; // Could be NumberFormatException or other issues
            }
        }
        if (path.equals("/api/venues") && (method.equals("GET") || method.equals("POST"))) {
            return "organizer".equals(role) || "admin".equals(role);
        }
        if (path.startsWith("/api/organizer/")) {
            return "organizer".equals(role);
        }
        if (path.equals("/api/admin/registrations") && method.equals("GET")) {
            return "admin".equals(role);
        }
        // For any other protected route, we can have a default
        return false;
    }
}
