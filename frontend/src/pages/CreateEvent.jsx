import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { eventApi, venueApi } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import { toast } from 'sonner';
import { Button } from '../components/components/ui/button';
import { Input } from '../components/components/ui/input';
import { Label } from '../components/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/components/ui/card';
import { Textarea } from '../components/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/components/ui/select';
import { CreateVenueDialog } from '../components/venues/CreateVenueDialog';

function CreateEventPage() {
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [eventDate, setEventDate] = useState('');
  const [venueId, setVenueId] = useState('');
  const [venues, setVenues] = useState([]);
  const [loadingVenues, setLoadingVenues] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchVenues = async () => {
      try {
        const response = await venueApi.getVenues();
        setVenues(response.data);
      } catch (error) {
        toast.error('Failed to load venues.');
      } finally {
        setLoadingVenues(false);
      }
    };

    if (user?.role === 'organizer' || user?.role === 'admin') {
      fetchVenues();
    }
  }, [user]);

  const handleVenueCreated = (newVenue) => {
    setVenues((prevVenues) => [...prevVenues, newVenue]);
    setVenueId(newVenue.venueId.toString());
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    if (!user || !user.userId) {
      toast.error('User not authenticated or ID not found.');
      setIsSubmitting(false);
      return;
    }

    try {
      const newEvent = {
        title,
        description,
        eventDate,
        venueId: parseInt(venueId),
        createdBy: user.userId,
      };
      await eventApi.createEvent(newEvent);
      toast.success('Event created successfully!');
      navigate('/events');
    } catch (error) {
      console.error('Error creating event:', error);
      toast.error('Failed to create event.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-4rem)]">
      <Card className="w-full max-w-lg">
        <CardHeader className="space-y-1">
          <CardTitle className="text-2xl">Create New Event</CardTitle>
          <CardDescription>Fill in the details to create a new event.</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="title">Event Title</Label>
              <Input
                id="title"
                type="text"
                placeholder="Tech Conference 2025"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                placeholder="A brief description of the event."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="eventDate">Event Date</Label>
              <Input
                id="eventDate"
                type="date"
                value={eventDate}
                onChange={(e) => setEventDate(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label>Venue</Label>
              <div className="flex items-center gap-2">
                <Select onValueChange={setVenueId} value={venueId} required>
                  <SelectTrigger>
                    <SelectValue placeholder={loadingVenues ? 'Loading venues...' : 'Select a venue'} />
                  </SelectTrigger>
                  <SelectContent>
                    {venues.map((venue) => (
                      <SelectItem key={venue.venueId} value={venue.venueId.toString()}>
                        {venue.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <CreateVenueDialog onVenueCreated={handleVenueCreated} />
              </div>
            </div>
            <Button type="submit" className="w-full bg-purple-600 hover:bg-purple-700" disabled={isSubmitting}>
              {isSubmitting ? 'Creating...' : 'Create Event'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

export default CreateEventPage;
