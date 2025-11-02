import { Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { format } from 'date-fns';
import { CalendarIcon, MapPinIcon } from 'lucide-react';

function EventCard({ event }) {
  return (
    <Link to={`/events/${event.eventId}`} className="block transition-transform duration-200 hover:scale-105">
      <Card className="flex flex-col justify-between h-full overflow-hidden shadow-md hover:shadow-xl">
        <CardHeader>
          <CardTitle className="text-xl font-bold tracking-tight">{event.title}</CardTitle>
          <CardDescription className="line-clamp-2">{event.description}</CardDescription>
        </CardHeader>
        <CardContent className="flex-grow">
          <div className="flex items-center text-sm text-gray-500 mb-2">
            <CalendarIcon className="mr-2 h-4 w-4" />
            <span>{format(new Date(event.eventDate), 'PPP')}</span>
          </div>
          <div className="flex items-center text-sm text-gray-500">
            <MapPinIcon className="mr-2 h-4 w-4" />
            <span>{event.venue ? event.venue.name : 'Venue TBD'}</span>
          </div>
        </CardContent>
        <CardFooter>
          <Button asChild className="w-full bg-purple-600 hover:bg-purple-700">
            <div className="w-full">View Details</div>
          </Button>
        </CardFooter>
      </Card>
    </Link>
  );
}

export default EventCard;
