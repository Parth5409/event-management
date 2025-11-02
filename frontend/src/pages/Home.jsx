import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/components/ui/button';
import { eventApi } from '../lib/api';
import EventCard from '../components/events/EventCard';

function HomePage() {
  const [featuredEvents, setFeaturedEvents] = useState([]);

  useEffect(() => {
    const fetchFeaturedEvents = async () => {
      try {
        const response = await eventApi.getAllEvents();
        // Take the first 3 events as featured
        setFeaturedEvents(response.data.slice(0, 3));
      } catch (error) {
        console.error("Failed to load featured events:", error);
      }
    };
    fetchFeaturedEvents();
  }, []);

  return (
    <div className="min-h-[calc(100vh-4rem)]">
      {/* Hero Section */}
      <div className="flex flex-col items-center justify-center text-center px-4 py-12 md:py-24 lg:py-32">
        <div className="space-y-6">
          <h1 className="text-4xl font-bold tracking-tighter sm:text-5xl md:text-6xl lg:text-7xl/none bg-gradient-to-r from-purple-600 to-indigo-600 text-transparent bg-clip-text">
            Discover & Experience Unforgettable Events
          </h1>
          <p className="mx-auto max-w-[700px] text-gray-500 md:text-xl dark:text-gray-400">
            EventFlow is your go-to platform for finding and managing amazing events. From tech conferences to music festivals, we've got you covered.
          </p>
          <div className="space-x-4">
            <Button asChild size="lg" className="bg-purple-600 hover:bg-purple-700">
              <Link to="/events">Browse Events</Link>
            </Button>
            <Button asChild variant="outline" size="lg">
              <Link to="/register">Join EventFlow</Link>
            </Button>
          </div>
        </div>
      </div>

      {/* Featured Events Section */}
      {featuredEvents.length > 0 && (
        <div className="container mx-auto px-4 py-12">
          <h2 className="text-3xl font-bold text-center mb-8">Featured Events</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {featuredEvents.map(event => (
              <EventCard key={event.eventId} event={event} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default HomePage;
