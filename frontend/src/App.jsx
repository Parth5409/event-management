import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Toaster } from './components/components/ui/sonner';

// Import Pages
import HomePage from './pages/Home';
import LoginPage from './pages/Login';
import RegisterPage from './pages/Register';
import EventsPage from './pages/Events';
import EventDetailsPage from './pages/EventDetailsPage';
import CreateEventPage from './pages/CreateEvent';
import MyRegistrationsPage from './pages/MyRegistrations';
import OrganizerDashboard from './pages/OrganizerDashboard';
import EventRegistrationsPage from './pages/EventRegistrations';

// Import Layout Components
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';

// Import Auth Components
import ProtectedRoute from './components/auth/ProtectedRoute';

function App() {
  return (
    <Router>
      <div className="min-h-screen flex flex-col">
        <Navbar />
        <main className="flex-grow">
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/events" element={<EventsPage />} />
            <Route path="/events/:id" element={<EventDetailsPage />} />

            {/* Protected Routes */}
            <Route path="/events/new" element={<ProtectedRoute allowedRoles={['organizer', 'admin']}><CreateEventPage /></ProtectedRoute>} />
            <Route path="/my-registrations" element={<ProtectedRoute allowedRoles={['attendee', 'organizer', 'admin']}><MyRegistrationsPage /></ProtectedRoute>} />
            <Route path="/organizer/dashboard" element={<ProtectedRoute allowedRoles={['organizer']}><OrganizerDashboard /></ProtectedRoute>} />
            <Route path="/organizer/events/:id/registrations" element={<ProtectedRoute allowedRoles={['organizer']}><EventRegistrationsPage /></ProtectedRoute>} />
          </Routes>
        </main>
        <Footer />
      </div>
      <Toaster />
    </Router>
  );
}

export default App;