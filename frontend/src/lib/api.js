import axios from 'axios';
import { toast } from 'sonner';
import { useAuthStore } from '../store/authStore';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling and 401 redirect
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const { response } = error;
    if (response) {
      if (response.status === 401) {
        // Unauthorized, redirect to login
        useAuthStore.getState().logout();
        window.location.href = '/login';
        toast.error('Session expired or unauthorized. Please log in again.');
      } else if (response.data && response.data.message) {
        toast.error(response.data.message);
      } else {
        toast.error('An unexpected error occurred.');
      }
    } else {
      toast.error('Network error or server unreachable.');
    }
    return Promise.reject(error);
  }
);

// --- Authentication API ---
export const authApi = {
  register: (userData) => api.post('/auth/register', userData),
  login: (credentials) => api.post('/auth/login', credentials),
  getUser: (userId) => api.get(`/users/${userId}`),
};

// --- Event API ---
export const eventApi = {
  getAllEvents: () => api.get('/events'),
  getEventById: (id) => api.get(`/events/${id}`),
  createEvent: (eventData) => api.post('/events', eventData),
  updateEvent: (id, eventData) => api.put(`/events/${id}`, eventData),
  deleteEvent: (id) => api.delete(`/events/${id}`),
};

// --- Venue API ---
export const venueApi = {
  getVenues: () => api.get('/venues'),
  createVenue: (venueData) => api.post('/venues', venueData),
};

// --- Organizer API ---
export const organizerApi = {
  getOrganizerEvents: () => api.get('/organizer/events'),
  getEventRegistrations: (eventId) => api.get(`/organizer/events/${eventId}/registrations`),
};

// --- Registration API ---
export const registrationApi = {
  registerForEvent: (eventId, userId) => api.post(`/events/${eventId}/register`, { userId }),
  getUserRegistrations: (userId) => api.get(`/users/${userId}/registrations`),
  getEventRegistrations: (eventId) => api.get(`/admin/registrations?eventId=${eventId}`),
};

export default api;
