import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { eventApi, registrationApi } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import { toast } from 'sonner';
import { Button } from '../components/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/components/ui/card';
import { Skeleton } from '../components/components/ui/skeleton';
import { format } from 'date-fns';
import { CalendarIcon, MapPinIcon, UserIcon } from 'lucide-react';

function EventDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuthStore();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isRegistering, setIsRegistering] = useState(false);

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        const response = await eventApi.getEventById(id);
        setEvent(response.data);
      } catch (err) {
        setError(err);
        toast.error('Failed to load event details.');
      } finally {
        setLoading(false);
      }
    };

    fetchEvent();
  }, [id]);

  const handleRegister = async () => {
    if (!isAuthenticated || !user) {
      toast.error('Please log in to register for an event.');
      navigate('/login');
      return;
    }
    if (user.role !== 'attendee') {
      toast.error('Only attendees can register for events.');
      return;
    }

    setIsRegistering(true);
    try {
      await registrationApi.registerForEvent(id, user.userId);
      toast.success('Successfully registered for the event!');
    } catch (err) {
      console.error('Registration error:', err);
    } finally {
      setIsRegistering(false);
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="grid md:grid-cols-3 gap-8">
          <div className="md:col-span-2 space-y-4">
            <Skeleton className="h-10 w-3/4" />
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-5/6" />
            <Skeleton className="h-12 w-40 mt-4" />
          </div>
          <div className="space-y-4">
            <Skeleton className="h-48 w-full rounded-lg" />
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return <div className="text-center text-red-500">Error: {error.message}</div>;
  }

  if (!event) {
    return <div className="text-center text-gray-500">Event not found.</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="grid md:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="md:col-span-2">
          <h1 className="text-4xl font-bold tracking-tight mb-4">{event.title}</h1>
          <p className="text-lg text-gray-600 mb-6">{event.description}</p>
          
          {isAuthenticated && user?.role === 'attendee' && (
            <Button 
              onClick={handleRegister} 
              disabled={isRegistering} 
              size="lg"
              className="bg-purple-600 hover:bg-purple-700"
            >
              {isRegistering ? 'Registering...' : 'Register for Event'}
            </Button>
          )}
          {!isAuthenticated && (
            <p className="text-gray-500">Please <Link to="/login" className="underline text-purple-600">log in</Link> to register for this event.</p>
          )}
          {isAuthenticated && user?.role !== 'attendee' && (
            <p className="text-gray-500">Only attendees can register for events.</p>
          )}
        </div>

        {/* Details Card */}
        <div>
          <Card>
            <CardHeader>
              <CardTitle>Event Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-start">
                <CalendarIcon className="h-5 w-5 mr-3 mt-1 text-gray-500" />
                <div>
                  <p className="font-semibold">Date & Time</p>
                  <p className="text-gray-600">{format(new Date(event.eventDate), 'PPP')}</p>
                </div>
              </div>
              <div className="flex items-start">
                <MapPinIcon className="h-5 w-5 mr-3 mt-1 text-gray-500" />
                <div>
                  <p className="font-semibold">Venue</p>
                  <p className="text-gray-600">{event.venue ? event.venue.name : 'N/A'}</p>
                  <p className="text-sm text-gray-500">{event.venue ? event.venue.location : ''}</p>
                </div>
              </div>
              <div className="flex items-start">
                <UserIcon className="h-5 w-5 mr-3 mt-1 text-gray-500" />
                <div>
                  <p className="font-semibold">Organizer</p>
                  {/* This assumes you might add organizer details to the event object in the future */}
                  <p className="text-gray-600">Organizer ID: {event.createdBy}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}

export default EventDetailsPage;
