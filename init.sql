-- ============================================
-- EventFlow Database Schema
-- Complete Setup Script
-- ============================================

-- Connect to PostgreSQL as postgres user first
-- psql -U postgres

-- Drop database if exists (CAREFUL!)
-- DROP DATABASE IF EXISTS eventdb;

-- Create database
-- CREATE DATABASE eventdb;

-- Drop existing tables if they exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS registrations CASCADE;
DROP TABLE IF EXISTS event_schedule CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS venues CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================
-- 1. USERS TABLE
-- ============================================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'attendee' CHECK (role IN ('attendee', 'organizer', 'admin')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster email lookups
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

COMMENT ON TABLE users IS 'Stores user information for attendees, organizers, and admins';
COMMENT ON COLUMN users.role IS 'User role: attendee, organizer, or admin';

-- ============================================
-- 2. VENUES TABLE
-- ============================================
CREATE TABLE venues (
    venue_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location TEXT,
    capacity INTEGER CHECK (capacity > 0),
    created_by INTEGER REFERENCES users(user_id) ON DELETE SET NULL
);

-- Index for searching venues
CREATE INDEX idx_venues_name ON venues(name);
CREATE INDEX idx_venues_creator ON venues(created_by);

COMMENT ON TABLE venues IS 'Stores venue information for events';

-- ============================================
-- 3. EVENTS TABLE
-- ============================================
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    venue_id INTEGER REFERENCES venues(venue_id) ON DELETE SET NULL,
    created_by INTEGER REFERENCES users(user_id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_events_venue ON events(venue_id);
CREATE INDEX idx_events_creator ON events(created_by);
CREATE INDEX idx_events_title ON events(title);

COMMENT ON TABLE events IS 'Stores event information';

-- ============================================
-- 4. EVENT_SCHEDULE TABLE (Optional - for multi-session events)
-- ============================================
CREATE TABLE event_schedule (
    schedule_id SERIAL PRIMARY KEY,
    event_id INTEGER REFERENCES events(event_id) ON DELETE CASCADE,
    session_title VARCHAR(255),
    session_start TIMESTAMP,
    session_end TIMESTAMP
);

CREATE INDEX idx_schedule_event ON event_schedule(event_id);

COMMENT ON TABLE event_schedule IS 'Stores multiple sessions for events';

-- ============================================
-- 5. REGISTRATIONS TABLE
-- ============================================
CREATE TABLE registrations (
    reg_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    event_id INTEGER NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'confirmed' CHECK (status IN ('confirmed', 'cancelled', 'waitlisted')),
    UNIQUE(user_id, event_id)
);

-- Indexes for better query performance
CREATE INDEX idx_registrations_user ON registrations(user_id);
CREATE INDEX idx_registrations_event ON registrations(event_id);
CREATE INDEX idx_registrations_status ON registrations(status);

COMMENT ON TABLE registrations IS 'Stores event registrations';
COMMENT ON CONSTRAINT registrations_user_id_event_id_key ON registrations IS 'Prevents duplicate registrations';

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- Sample Users (passwords are hashed with BCrypt - these are "password123")
-- Note: You'll need to register users through the application for proper password hashing
-- These are just placeholders

INSERT INTO users (full_name, email, password, role) VALUES
('Admin User', 'admin@eventflow.com', '$2a$10$rJLqkeZqkl5L5L5L5L5L5eK5L5L5L5L5L5L5L5L5L5L5L5L5L5L5L', 'admin'),
('John Organizer', 'organizer@eventflow.com', '$2a$10$rJLqkeZqkl5L5L5L5L5L5eK5L5L5L5L5L5L5L5L5L5L5L5L5L5L5L', 'organizer'),
('Sarah Attendee', 'attendee@eventflow.com', '$2a$10$rJLqkeZqkl5L5L5L5L5L5eK5L5L5L5L5L5L5L5L5L5L5L5L5L5L5L', 'attendee');

-- Sample Venues
INSERT INTO venues (name, location, capacity, created_by) VALUES
('Grand Auditorium', 'Main Campus Building, Room 101', 500, 2),
('Conference Hall A', 'Business Center, 2nd Floor', 200, 2),
('Open Air Arena', 'Central Park Grounds', 1000, 2),
('Tech Hub Seminar Room', 'Innovation Center, 3rd Floor', 50, 2),
('City Convention Center', 'Downtown Plaza, Hall B', 800, 2),
('University Library Hall', 'Central Library, 1st Floor', 150, 2),
('Sports Complex Arena', 'Athletics Building', 2000, 2);

-- Sample Events
INSERT INTO events (title, description, event_date, venue_id, created_by) VALUES
('Tech Conference 2025', 'Annual technology conference featuring the latest innovations in software development, AI, and cloud computing.', '2025-12-15', 1, 2),
('Startup Pitch Night', 'Local startups present their business ideas to investors and the community.', '2025-11-20', 2, 2),
('Music Festival', 'Outdoor music festival featuring local and international artists across multiple genres.', '2025-11-30', 3, 2),
('AI Workshop', 'Hands-on workshop on machine learning and artificial intelligence fundamentals.', '2025-12-05', 4, 2),
('Business Summit', 'Annual business summit for entrepreneurs and business leaders.', '2026-01-10', 5, 2),
('Career Fair 2025', 'Connect with top employers and explore career opportunities.', '2025-12-01', 1, 2),
('Coding Bootcamp', 'Intensive 3-day coding bootcamp for beginners.', '2025-12-18', 4, 2);

-- Sample Registrations
INSERT INTO registrations (user_id, event_id, status) VALUES
(3, 1, 'confirmed'),
(3, 2, 'confirmed'),
(3, 4, 'confirmed');

-- ============================================
-- VERIFY DATA
-- ============================================

-- Display counts
SELECT 'Users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'Venues', COUNT(*) FROM venues
UNION ALL
SELECT 'Events', COUNT(*) FROM events
UNION ALL
SELECT 'Registrations', COUNT(*) FROM registrations;

-- Display sample data
SELECT '--- USERS ---' AS info;
SELECT user_id, full_name, email, role FROM users;

SELECT '--- VENUES ---' AS info;
SELECT venue_id, name, location, capacity FROM venues;

SELECT '--- EVENTS ---' AS info;
SELECT event_id, title, event_date, venue_id FROM events;

SELECT '--- REGISTRATIONS ---' AS info;
SELECT reg_id, user_id, event_id, registered_at FROM registrations;

-- ============================================
-- SUCCESS MESSAGE
-- ============================================
SELECT 'Database setup completed successfully!' AS status;
