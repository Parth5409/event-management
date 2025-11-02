package com.eventflow.service;

import com.eventflow.dao.VenueDao;
import com.eventflow.model.Venue;

import java.util.List;

public class VenueService {
    private final VenueDao venueDao = new VenueDao();

    public Venue createVenue(Venue venue, int organizerId) {
        venue.setCreatedBy(organizerId);
        return venueDao.createVenue(venue);
    }

    public List<Venue> getVenuesByOrganizerId(int organizerId) {
        return venueDao.getVenuesByOrganizerId(organizerId);
    }
}
