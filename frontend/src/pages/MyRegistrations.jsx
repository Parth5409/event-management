import { useEffect, useState } from 'react';
import { useAuthStore } from '../store/authStore';
import { registrationApi } from '../lib/api';
import { toast } from 'sonner';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/components/ui/card';
import { Skeleton } from '../components/components/ui/skeleton';
import { format } from 'date-fns';

function MyRegistrationsPage() {
  const { user, isAuthenticated } = useAuthStore();
  const [registrations, setRegistrations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated || !user?.userId) {
      setLoading(false);
      return;
    }

    const fetchRegistrations = async () => {
      try {
        const response = await registrationApi.getUserRegistrations(user.userId);
        setRegistrations(response.data);
      } catch (err) {
        setError(err);
        toast.error('Failed to load your registrations.');
      } finally {
        setLoading(false);
      }
    };

    fetchRegistrations();
  }, [isAuthenticated, user]);

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-center mb-8">My Registrations</h1>
        <div className="grid grid-cols-1 gap-4">
          {[...Array(3)].map((_, i) => (
            <Card key={i}>
              <CardHeader>
                <Skeleton className="h-6 w-3/4 mb-2" />
                <Skeleton className="h-4 w-1/2" />
              </CardHeader>
              <CardContent>
                <Skeleton className="h-4 w-full" />
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return <div className="text-center text-red-500">Error: {error.message}</div>;
  }

  if (!isAuthenticated) {
    return <div className="text-center text-gray-500">Please log in to view your registrations.</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-center mb-8">My Registrations</h1>
      {registrations.length === 0 ? (
        <p className="text-center text-gray-500">You have not registered for any events yet.</p>
      ) : (
        <div className="grid grid-cols-1 gap-4">
          {registrations.map((reg) => (
            <Card key={reg.regId}>
              <CardHeader>
                <CardTitle>{reg.title}</CardTitle>
                <CardDescription>{reg.description}</CardDescription>
              </CardHeader>
              <CardContent>
                <p><strong>Event Date:</strong> {format(new Date(reg.eventDate), 'PPP')}</p>
                <p><strong>Registered At:</strong> {format(new Date(reg.registeredAt), 'PPP p')}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

export default MyRegistrationsPage;
