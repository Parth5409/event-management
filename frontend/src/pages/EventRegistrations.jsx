import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { organizerApi } from '../lib/api';
import { toast } from 'sonner';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '../components/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/components/ui/table';
import { format } from 'date-fns';
import { Button } from '../components/components/ui/button';

function EventRegistrationsPage() {
  const { id } = useParams();
  const [registrations, setRegistrations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRegistrations = async () => {
      try {
        const response = await organizerApi.getEventRegistrations(id);
        setRegistrations(response.data);
      } catch (error) {
        toast.error('Failed to load event registrations.');
      } finally {
        setLoading(false);
      }
    };

    fetchRegistrations();
  }, [id]);

  if (loading) {
    return <div>Loading registrations...</div>;
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-8">
        <Button asChild variant="outline">
          <Link to="/organizer/dashboard">← Back to Dashboard</Link>
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Event Registrations</CardTitle>
          <CardDescription>
            A total of {registrations.length} user(s) have registered for this event.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Full Name</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Registered At</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {registrations.length > 0 ? (
                registrations.map((reg) => (
                  <TableRow key={reg.regId}>
                    <TableCell className="font-medium">{reg.fullName}</TableCell>
                    <TableCell>{reg.email}</TableCell>
                    <TableCell>{format(new Date(reg.registeredAt), 'PPP p')}</TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={3} className="text-center">No registrations found for this event.</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}

export default EventRegistrationsPage;