package com.eventflow.dao;

import com.eventflow.model.Registration;
import com.eventflow.model.RegistrationDetails;
import com.eventflow.util.HikariCPDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDao {

    public Registration createRegistration(Registration registration) {
        String sql = "INSERT INTO public.registrations (user_id, event_id) VALUES (?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, registration.getUserId());
            pstmt.setInt(2, registration.getEventId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    registration.setRegId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating registration: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create registration", e);
        }
        return registration;
    }

    public List<Registration> getRegistrationsByUserId(int userId) {
        String sql = "SELECT * FROM public.registrations WHERE user_id = ?";
        List<Registration> registrations = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Registration registration = new Registration();
                    registration.setRegId(rs.getInt("reg_id"));
                    registration.setUserId(rs.getInt("user_id"));
                    registration.setEventId(rs.getInt("event_id"));
                    registration.setRegisteredAt(rs.getTimestamp("registered_at"));
                    registrations.add(registration);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting registrations by user ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get registrations by user ID", e);
        }
        return registrations;
    }

    public List<RegistrationDetails> getRegistrationDetailsByUserId(int userId) {
        String sql = "SELECT r.reg_id, r.registered_at, e.event_id, e.title, e.description, e.event_date " +
                     "FROM public.registrations r " +
                     "JOIN public.events e ON r.event_id = e.event_id " +
                     "WHERE r.user_id = ?";
        List<RegistrationDetails> details = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RegistrationDetails detail = new RegistrationDetails();
                    detail.setRegId(rs.getInt("reg_id"));
                    detail.setRegisteredAt(rs.getTimestamp("registered_at"));
                    detail.setEventId(rs.getInt("event_id"));
                    detail.setTitle(rs.getString("title"));
                    detail.setDescription(rs.getString("description"));
                    detail.setEventDate(rs.getDate("event_date"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting registration details by user ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get registration details by user ID", e);
        }
        return details;
    }

    public List<Registration> getRegistrationsByEventId(int eventId) {
        String sql = "SELECT * FROM public.registrations WHERE event_id = ?";
        List<Registration> registrations = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Registration registration = new Registration();
                    registration.setRegId(rs.getInt("reg_id"));
                    registration.setUserId(rs.getInt("user_id"));
                    registration.setEventId(rs.getInt("event_id"));
                    registration.setRegisteredAt(rs.getTimestamp("registered_at"));
                    registrations.add(registration);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting registrations by event ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get registrations by event ID", e);
        }
        return registrations;
    }

    public List<RegistrationDetails> getRegistrationDetailsByEventId(int eventId) {
        String sql = "SELECT r.reg_id, r.registered_at, u.user_id, u.full_name, u.email " +
                     "FROM public.registrations r " +
                     "JOIN public.users u ON r.user_id = u.user_id " +
                     "WHERE r.event_id = ?";
        List<RegistrationDetails> details = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, eventId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RegistrationDetails detail = new RegistrationDetails();
                    detail.setRegId(rs.getInt("reg_id"));
                    detail.setRegisteredAt(rs.getTimestamp("registered_at"));
                    detail.setUserId(rs.getInt("user_id"));
                    detail.setFullName(rs.getString("full_name"));
                    detail.setEmail(rs.getString("email"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get registration details by event ID", e);
        }
        return details;
    }
}
