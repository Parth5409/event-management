package com.eventflow.dao;

import com.eventflow.model.Venue;
import com.eventflow.util.HikariCPDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VenueDao {

    public Venue createVenue(Venue venue) {
        String sql = "INSERT INTO public.venues (name, location, capacity, created_by) VALUES (?, ?, ?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, venue.getName());
            pstmt.setString(2, venue.getLocation());
            pstmt.setInt(3, venue.getCapacity());
            pstmt.setInt(4, venue.getCreatedBy());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venue.setVenueId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create venue", e);
        }
        return venue;
    }

    public List<Venue> getVenuesByOrganizerId(int organizerId) {
        String sql = "SELECT * FROM public.venues WHERE created_by = ?";
        List<Venue> venues = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Venue venue = new Venue();
                    venue.setVenueId(rs.getInt("venue_id"));
                    venue.setName(rs.getString("name"));
                    venue.setLocation(rs.getString("location"));
                    venue.setCapacity(rs.getInt("capacity"));
                    venue.setCreatedBy(rs.getInt("created_by"));
                    venues.add(venue);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get venues by organizer", e);
        }
        return venues;
    }
    
    public Venue getVenueById(int venueId) {
        String sql = "SELECT * FROM public.venues WHERE venue_id = ?";
        Venue venue = null;

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, venueId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    venue = new Venue();
                    venue.setVenueId(rs.getInt("venue_id"));
                    venue.setName(rs.getString("name"));
                    venue.setLocation(rs.getString("location"));
                    venue.setCapacity(rs.getInt("capacity"));
                    venue.setCreatedBy(rs.getInt("created_by"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get venue by ID", e);
        }
        return venue;
    }
}
