package com.eventflow.service;

import com.eventflow.dao.RegistrationDao;
import com.eventflow.model.Registration;
import com.eventflow.model.RegistrationDetails;

import java.util.List;

public class RegistrationService {
    private RegistrationDao registrationDao = new RegistrationDao();

    public Registration createRegistration(Registration registration) {
        return registrationDao.createRegistration(registration);
    }

    public List<Registration> getRegistrationsByUserId(int userId) {
        return registrationDao.getRegistrationsByUserId(userId);
    }

    public List<RegistrationDetails> getRegistrationDetailsByUserId(int userId) {
        return registrationDao.getRegistrationDetailsByUserId(userId);
    }

    public List<Registration> getRegistrationsByEventId(int eventId) {
        return registrationDao.getRegistrationsByEventId(eventId);
    }

    public List<RegistrationDetails> getRegistrationDetailsByEventId(int eventId) {
        return registrationDao.getRegistrationDetailsByEventId(eventId);
    }
}
