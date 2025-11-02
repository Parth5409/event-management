package com.eventflow.service;

import com.eventflow.dao.EventDao;
import com.eventflow.model.Event;
import java.util.List;

public class EventService {
    private EventDao eventDao = new EventDao();

    public List<Event> getAllEvents() {
        return eventDao.getAllEvents();
    }

    public Event getEventById(int id) {
        return eventDao.getEventById(id);
    }

    public Event createEvent(Event event) {
        return eventDao.createEvent(event);
    }

    public Event updateEvent(Event event) {
        return eventDao.updateEvent(event);
    }

    public void deleteEvent(int id) {
        eventDao.deleteEvent(id);
    }

    public List<Event> getEventsByOrganizerId(int organizerId) {
        return eventDao.getEventsByOrganizerId(organizerId);
    }

    public boolean isEventOwner(int userId, int eventId) {
        Event event = eventDao.getEventById(eventId);
        return event != null && event.getCreatedBy() == userId;
    }
}
