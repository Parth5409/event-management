import { useState } from 'react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
  SheetFooter,
  SheetClose,
} from '../components/ui/sheet';
import { venueApi } from '../../lib/api';
import { toast } from 'sonner';

export function CreateVenueDialog({ onVenueCreated }) {
  const [name, setName] = useState('');
  const [location, setLocation] = useState('');
  const [capacity, setCapacity] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const newVenue = {
        name,
        location,
        capacity: parseInt(capacity),
      };
      const response = await venueApi.createVenue(newVenue);
      toast.success('Venue created successfully!');
      onVenueCreated(response.data); // Pass the new venue back to the parent
      // Reset form
      setName('');
      setLocation('');
      setCapacity('');
    } catch (error) {
      toast.error('Failed to create venue.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button variant="outline">Create New Venue</Button>
      </SheetTrigger>
      <SheetContent>
        <SheetHeader>
          <SheetTitle>Create New Venue</SheetTitle>
          <SheetDescription>
            Add a new venue for your events. Click save when you're done.
          </SheetDescription>
        </SheetHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="name" className="text-right">
                Name
              </Label>
              <Input
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="col-span-3"
                required
              />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="location" className="text-right">
                Location
              </Label>
              <Input
                id="location"
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                className="col-span-3"
                required
              />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="capacity" className="text-right">
                Capacity
              </Label>
              <Input
                id="capacity"
                type="number"
                value={capacity}
                onChange={(e) => setCapacity(e.target.value)}
                className="col-span-3"
                required
              />
            </div>
          </div>
          <SheetFooter>
            <SheetClose asChild>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Saving...' : 'Save changes'}
              </Button>
            </SheetClose>
          </SheetFooter>
        </form>
      </SheetContent>
    </Sheet>
  );
}
