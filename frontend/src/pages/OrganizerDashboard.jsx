import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { organizerApi, eventApi } from '../lib/api'; // Assuming you'll create this
import { useAuthStore } from '../store/authStore';
import { toast } from 'sonner';
import { Button } from '../components/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/components/ui/table';
import { format } from 'date-fns';

function OrganizerDashboard() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuthStore();
  const location = useLocation();

  useEffect(() => {
    const fetchEvents = async () => {
      if (!user?.userId) {
        setLoading(false);
        return;
      }

      try {
        const response = await organizerApi.getOrganizerEvents(); 
        setEvents(response.data);
      } catch (error) {
        console.error('OrganizerDashboard: Failed to load your events:', error);
        toast.error('Failed to load your events.');
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [user?.userId, location.pathname]);

  const handleDelete = async (eventId) => {
    if (!window.confirm('Are you sure you want to delete this event?')) return;
    try {
      // This uses the existing eventApi
      await eventApi.deleteEvent(eventId);
      setEvents(events.filter(event => event.eventId !== eventId));
      toast.success('Event deleted successfully.');
    } catch (error) {
      toast.error('Failed to delete event.');
    }
  };

  if (loading) {
    return <div>Loading your events...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Organizer Dashboard</h1>
        <Button asChild className="bg-purple-600 hover:bg-purple-700">
          <Link to="/events/new">Create New Event</Link>
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>My Events</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Title</TableHead>
                <TableHead>Date</TableHead>
                <TableHead>Venue</TableHead>
                <TableHead>Registrations</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {events.length > 0 ? (
                events.map((event) => (
                  <TableRow key={event.eventId}>
                    <TableCell className="font-medium">{event.title}</TableCell>
                    <TableCell>{format(new Date(event.eventDate), 'PPP')}</TableCell>
                    <TableCell>{event.venue ? event.venue.name : 'N/A'}</TableCell>
                    <TableCell>{event.registrationCount}</TableCell>
                    <TableCell className="space-x-2">
                      <Button asChild variant="outline" size="sm">
                        <Link to={`/organizer/events/${event.eventId}/registrations`}>Manage</Link>
                      </Button>
                      <Button variant="destructive" size="sm" onClick={() => handleDelete(event.eventId)}>
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={5} className="text-center">You have not created any events yet.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}

export default OrganizerDashboard;