package com.eventflow.dao;

import com.eventflow.model.Event;
import com.eventflow.model.Venue;
import com.eventflow.util.HikariCPDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDao {

    public List<Event> getAllEvents() {
        String sql = "SELECT e.*, v.name as venue_name, v.location as venue_location, v.capacity as venue_capacity " +
                     "FROM public.events e " +
                     "LEFT JOIN public.venues v ON e.venue_id = v.venue_id " +
                     "ORDER BY e.event_date DESC";
        List<Event> events = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setTitle(rs.getString("title"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("event_date"));
                event.setVenueId(rs.getInt("venue_id"));
                event.setCreatedBy(rs.getInt("created_by"));
                event.setCreatedAt(rs.getTimestamp("created_at"));

                if (rs.getInt("venue_id") != 0) {
                    Venue venue = new Venue();
                    venue.setVenueId(rs.getInt("venue_id"));
                    venue.setName(rs.getString("venue_name"));
                    venue.setLocation(rs.getString("venue_location"));
                    venue.setCapacity(rs.getInt("venue_capacity"));
                    event.setVenue(venue);
                }

                events.add(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all events", e);
        }
        return events;
    }

    public Event getEventById(int id) {
        String sql = "SELECT e.*, v.name as venue_name, v.location as venue_location, v.capacity as venue_capacity " +
                     "FROM public.events e " +
                     "LEFT JOIN public.venues v ON e.venue_id = v.venue_id " +
                     "WHERE e.event_id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Event event = new Event();
                    event.setEventId(rs.getInt("event_id"));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setVenueId(rs.getInt("venue_id"));
                    event.setCreatedBy(rs.getInt("created_by"));
                    event.setCreatedAt(rs.getTimestamp("created_at"));

                    if (rs.getInt("venue_id") != 0) {
                        Venue venue = new Venue();
                        venue.setVenueId(rs.getInt("venue_id"));
                        venue.setName(rs.getString("venue_name"));
                        venue.setLocation(rs.getString("venue_location"));
                        venue.setCapacity(rs.getInt("venue_capacity"));
                        event.setVenue(venue);
                    }
                    return event;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get event by ID", e);
        }
        return null;
    }

    public Event createEvent(Event event) {
        String sql = "INSERT INTO public.events (title, description, event_date, venue_id, created_by) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, event.getEventDate());
            pstmt.setInt(4, event.getVenueId());
            pstmt.setInt(5, event.getCreatedBy());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setEventId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create event", e);
        }
        return event;
    }

    public Event updateEvent(Event event) {
        String sql = "UPDATE public.events SET title = ?, description = ?, event_date = ?, venue_id = ? WHERE event_id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getTitle());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, event.getEventDate());
            pstmt.setInt(4, event.getVenueId());
            pstmt.setInt(5, event.getEventId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Event not found with ID: " + event.getEventId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update event", e);
        }
        return event;
    }

    public void deleteEvent(int id) {
        String sql = "DELETE FROM public.events WHERE event_id = ?";

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Event not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete event", e);
        }
    }

    public List<Event> getEventsByOrganizerId(int organizerId) {
        String sql = "SELECT e.*, v.name as venue_name, v.location as venue_location, v.capacity as venue_capacity, " +
                     "(SELECT COUNT(*) FROM public.registrations r WHERE r.event_id = e.event_id) as registration_count " +
                     "FROM public.events e " +
                     "LEFT JOIN public.venues v ON e.venue_id = v.venue_id " +
                     "WHERE e.created_by = ? ORDER BY e.event_date DESC";
        List<Event> events = new ArrayList<>();

        try (Connection conn = HikariCPDataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event();
                    event.setEventId(rs.getInt("event_id"));
                    event.setTitle(rs.getString("title"));
                    event.setDescription(rs.getString("description"));
                    event.setEventDate(rs.getDate("event_date"));
                    event.setVenueId(rs.getInt("venue_id"));
                    event.setCreatedBy(rs.getInt("created_by"));
                    event.setCreatedAt(rs.getTimestamp("created_at"));
                    event.setRegistrationCount(rs.getInt("registration_count"));

                    if (rs.getInt("venue_id") != 0) {
                        Venue venue = new Venue();
                        venue.setVenueId(rs.getInt("venue_id"));
                        venue.setName(rs.getString("venue_name"));
                        venue.setLocation(rs.getString("venue_location"));
                        venue.setCapacity(rs.getInt("venue_capacity"));
                        event.setVenue(venue);
                    }
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get events by organizer", e);
        }
        return events;
    }
}
