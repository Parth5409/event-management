import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { Button } from '../components/ui/button';
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuLabel, 
  DropdownMenuSeparator, 
  DropdownMenuTrigger 
} from '../components/ui/dropdown-menu';
import { CircleUser, Menu } from 'lucide-react';
import { Sheet, SheetContent, SheetTrigger } from '../components/ui/sheet';

function Navbar() {
  const { isAuthenticated, user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 flex h-16 items-center gap-4 border-b bg-background px-4 md:px-6">
      <nav className="hidden flex-col gap-6 text-lg font-medium md:flex md:flex-row md:items-center md:gap-5 md:text-sm lg:gap-6">
        <Link to="/" className="flex items-center gap-2 text-lg font-semibold md:text-base">
          <span className="sr-only">EventFlow</span>
          <span className="text-purple-600 font-bold">EventFlow</span>
        </Link>
        <Link to="/events" className="text-muted-foreground transition-colors hover:text-foreground">
          Events
        </Link>
        {isAuthenticated && user && (user.role === 'organizer' || user.role === 'admin') && (
          <Link to="/events/new" className="text-muted-foreground transition-colors hover:text-foreground">
            Create Event
          </Link>
        )}
      </nav>
      <Sheet>
        <SheetTrigger asChild>
          <Button
            variant="outline"
            size="icon"
            className="shrink-0 md:hidden"
          >
            <Menu className="h-5 w-5" />
            <span className="sr-only">Toggle navigation menu</span>
          </Button>
        </SheetTrigger>
        <SheetContent side="left">
          <nav className="grid gap-6 text-lg font-medium">
            <Link to="/" className="flex items-center gap-2 text-lg font-semibold">
              <span className="sr-only">EventFlow</span>
              <span className="text-purple-600 font-bold">EventFlow</span>
            </Link>
            <Link
              to="/events"
              className="text-muted-foreground hover:text-foreground"
            >
              Events
            </Link>
            {isAuthenticated && user && (user.role === 'organizer' || user.role === 'admin') && (
              <Link
                to="/events/new"
                className="text-muted-foreground hover:text-foreground"
              >
                Create Event
              </Link>
            )}
            {!isAuthenticated && (
              <>
                <Link to="/login" className="text-muted-foreground hover:text-foreground">
                  Login
                </Link>
                <Link to="/register" className="text-muted-foreground hover:text-foreground">
                  Register
                </Link>
              </>
            )}
          </nav>
        </SheetContent>
      </Sheet>
      <div className="flex w-full items-center gap-4 md:ml-auto md:gap-2 lg:gap-4">
        <div className="ml-auto flex-1 sm:flex-initial"></div>
        {isAuthenticated && user ? (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="secondary" size="icon" className="rounded-full">
                <CircleUser className="h-5 w-5" />
                <span className="sr-only">Toggle user menu</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuLabel>{user.fullName || 'My Account'}</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem asChild>
                <Link to="/my-registrations">My Registrations</Link>
              </DropdownMenuItem>
              {user.role === 'organizer' && (
                <DropdownMenuItem asChild>
                  <Link to="/organizer/dashboard">Organizer Dashboard</Link>
                </DropdownMenuItem>
              )}
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={handleLogout}>Logout</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        ) : (
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => navigate('/login')}>Login</Button>
            <Button onClick={() => navigate('/register')}>Register</Button>
          </div>
        )}
      </div>
    </header>
  );
}

export default Navbar;
