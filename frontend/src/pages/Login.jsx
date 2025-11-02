import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { authApi } from '../lib/api';
import { toast } from 'sonner';
import { Button } from '../components/components/ui/button';
import { Input } from '../components/components/ui/input';
import { Label } from '../components/components/ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/components/ui/card';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await authApi.login({ email, password });
      const { token } = response.data;

      await login(token);
      toast.success('Login successful!');
      navigate('/');
    } catch (error) {
      console.error('Login error:', error);
      // Error message is handled by the Axios interceptor in api.js
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-4rem)]">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <CardTitle className="text-2xl">Login</CardTitle>
          <CardDescription>Enter your email and password to access your account</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
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
            <Button type="submit" className="w-full bg-purple-600 hover:bg-purple-700">
              Login
            </Button>
          </form>
          <div className="mt-4 text-center text-sm">
            Don't have an account?{" "}
            <Link to="/register" className="underline text-purple-600 hover:text-purple-700">
              Sign Up
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

export default LoginPage;
