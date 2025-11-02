import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../lib/api';
import { toast } from 'sonner';
import { Button } from '../components/components/ui/button';
import { Input } from '../components/components/ui/input';
import { Label } from '../components/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/components/ui/card';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '../components/components/ui/select';

function RegisterPage() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('attendee'); // Default role
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authApi.register({ fullName, email, password, role });
      toast.success('Registration successful! Please log in.');
      navigate('/login');
    } catch (error) {
      console.error('Registration error:', error);
      // Error message is handled by the Axios interceptor in api.js
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-4rem)]">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <CardTitle className="text-2xl">Register</CardTitle>
          <CardDescription>Create your account to get started</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="fullName">Full Name</Label>
              <Input
                id="fullName"
                type="text"
                placeholder="John Doe"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="m@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="role">Role</Label>
              <Select value={role} onValueChange={setRole}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select a role" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="attendee">Attendee</SelectItem>
                  <SelectItem value="organizer">Organizer</SelectItem>
                  {/* <SelectItem value="admin">Admin</SelectItem> */}
                </SelectContent>
              </Select>
            </div>
            <Button type="submit" className="w-full bg-purple-600 hover:bg-purple-700">
              Register
            </Button>
          </form>
          <div className="mt-4 text-center text-sm">
            Already have an account?{" "}
            <Link to="/login" className="underline text-purple-600 hover:text-purple-700">
              Login
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

export default RegisterPage;
