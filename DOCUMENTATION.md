# EventFlow - Full-Stack Event Management System Documentation

## 1. Project Overview

EventFlow is a comprehensive, full-stack event management platform designed to connect event organizers and attendees. It allows organizers to create, manage, and monitor their events, while attendees can browse, discover, and register for them.

The application is built with a modern technology stack, featuring a robust Java backend and a reactive and responsive React frontend.

---

## 2. Technology Stack

### Backend
- **Language:** Java 21
- **Framework:** SparkJava (a lightweight, unopinionated web framework)
- **Database:** PostgreSQL (via JDBC)
- **Connection Pooling:** HikariCP for high-performance database connections.
- **Authentication:** JSON Web Tokens (JWT) for stateless, secure authentication.
- **Password Hashing:** jBCrypt for securely hashing user passwords.
- **JSON Processing:** Google Gson for serialization and deserialization.
- **Build Tool:** Apache Maven for dependency management and building.

### Frontend
- **Framework:** React 19 with Vite for a fast development experience.
- **Routing:** React Router for client-side routing.
- **Styling:** Tailwind CSS for a utility-first styling approach.
- **UI Components:** `shadcn/ui` for a set of accessible and reusable components.
- **State Management:** Zustand for minimalistic and powerful global state management.
- **HTTP Client:** Axios, with interceptors for centralized API requests and error handling.
- **Date Handling:** `date-fns` for reliable date formatting.
- **Notifications:** `sonner` for clean and simple toast notifications.

---

## 3. Backend Documentation (`/backend`)

The backend is a Java application built with SparkJava, following a layered architecture to ensure separation of concerns.

### 3.1. Architecture

The backend is organized into three primary layers:

1.  **Controller Layer (`com.eventflow.controller`):** Handles incoming HTTP requests, validates input, and orchestrates responses. It acts as the bridge between the web and the service layer.
2.  **Service Layer (`com.eventflow.service`):** Contains the core business logic of the application. It processes data, performs calculations, and coordinates with the DAO layer.
3.  **DAO (Data Access Object) Layer (`com.eventflow.dao`):** Responsible for all communication with the PostgreSQL database. It abstracts the SQL queries and provides a clean interface for the service layer to interact with data.

### 3.2. Project Structure

```
backend/
├── pom.xml                 # Maven build configuration and dependencies
└── src/
    └── main/
        ├── java/com/eventflow/
        │   ├── controller/ # API endpoint handlers
        │   ├── dao/        # Database access objects (SQL queries)
        │   ├── main/       # Main application entry point
        │   ├── model/      # Java objects representing data structures
        │   ├── service/    # Business logic
        │   └── util/       # Utility classes (Auth, DB Connection)
        └── resources/
            └── config.properties # Database connection configuration
```

### 3.3. Database Schema

The application uses a PostgreSQL database with the following tables:

#### `users`
Stores user account information, including credentials and roles.
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Stores a BCrypt hash
    role VARCHAR(20) DEFAULT 'attendee',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### `venues`
Stores information about event locations.
```sql
CREATE TABLE venues (
    venue_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location TEXT,
    capacity INTEGER,
    created_by INTEGER REFERENCES users(user_id)
);
```

#### `events`
The core table for event information.
```sql
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    venue_id INTEGER REFERENCES venues(venue_id),
    created_by INTEGER REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### `registrations`
A linking table that tracks which users have registered for which events.
```sql
CREATE TABLE registrations (
    reg_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    event_id INTEGER REFERENCES events(event_id) ON DELETE CASCADE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, event_id) -- Prevents a user from registering twice for the same event
);
```

### 3.4. API Endpoints

The API base URL is `http://localhost:8080/api`.

#### Authentication Endpoints (`/api/auth`)
- **`POST /api/auth/register`**
  - **Description:** Creates a new user account.
  - **Request Body:** `{ "fullName": "...", "email": "...", "password": "...", "role": "attendee|organizer" }`
  - **Success Response:** `201 Created` with the new user object (password excluded).
- **`POST /api/auth/login`**
  - **Description:** Authenticates a user and returns a JWT.
  - **Request Body:** `{ "email": "...", "password": "..." }`
  - **Success Response:** `200 OK` with `{ "token": "..." }`.
  - **Error Response:** `401 Unauthorized` for invalid credentials.

#### User Endpoints (`/api/users`)
- **`GET /api/users/:id`**
  - **Description:** Retrieves a user's public profile information.
  - **Permissions:** Authenticated.
  - **Success Response:** `200 OK` with the user object (password excluded).

#### Event Endpoints (`/api/events`)
- **`GET /api/events`**: Fetches a list of all events.
- **`GET /api/events/:id`**: Fetches details for a single event.
- **`POST /api/events`**: Creates a new event. (Requires `organizer` or `admin` role).
- **`PUT /api/events/:id`**: Updates an existing event. (Requires `organizer` or `admin` role).
- **`DELETE /api/events/:id`**: Deletes an event. (Requires `organizer` or `admin` role).

#### Venue Endpoints (`/api/venues`)
- **`GET /api/venues`**: Fetches all venues created by the logged-in organizer. (Requires `organizer` or `admin` role).
- **`POST /api/venues`**: Creates a new venue. (Requires `organizer` or `admin` role).

#### Registration Endpoints
- **`POST /api/events/:id/register`**: Registers the logged-in user for an event. (Requires `attendee` role).
- **`GET /api/users/:id/registrations`**: Fetches all events a specific user is registered for. (Requires authentication).

#### Organizer Endpoints (`/api/organizer`)
- **`GET /api/organizer/events`**: Fetches all events created by the logged-in organizer, including a count of registrations for each. (Requires `organizer` role).
- **`GET /api/organizer/events/:id/registrations`**: Fetches a list of all users registered for a specific event owned by the organizer. (Requires `organizer` role).

### 3.5. Authentication Flow

1.  **Registration:** A user provides their details. The backend hashes their password using **BCrypt** and stores the new user record.
2.  **Login:** The user provides their email and password. The backend finds the user by email and uses `BCrypt.checkpw` to compare the provided password with the stored hash.
3.  **Token Generation:** Upon successful login, a **JWT** is generated containing the `userId` and `role`.
4.  **Authenticated Requests:** The frontend includes this JWT in the `Authorization: Bearer <token>` header for all protected requests.
5.  **Middleware Validation:** The `AuthMiddleware` on the backend intercepts protected requests, validates the JWT, and checks if the user's role grants them permission to access the requested resource.

### 3.6. Configuration & Running

- **Configuration:** Database connection details are stored in `backend/src/main/resources/config.properties`.
- **Build:** `cd backend && mvn clean package`
- **Run:** `cd backend && java -jar target/event-management-backend-1.0.0.jar`

---

## 4. Frontend Documentation (`/frontend`)

The frontend is a modern single-page application (SPA) built with React and Vite.

### 4.1. Architecture

The frontend follows a standard component-based architecture.

-   **Pages (`src/pages`):** Top-level components that correspond to different URL routes (e.g., `HomePage`, `LoginPage`).
-   **Components (`src/components`):** Reusable UI elements. This includes:
    -   **Layout:** `Navbar`, `Footer`.
    -   **UI:** Generic, unstyled components from `shadcn/ui` (`Button`, `Card`, etc.).
    -   **Feature-Specific:** Components tied to a specific feature, like `EventCard`.
-   **Services (`src/lib`):** Contains the API client (`api.js`) and other utilities.
-   **State (`src/store`):** Global state management using Zustand.

### 4.2. Project Structure

```
frontend/
├── package.json            # NPM dependencies and scripts
├── vite.config.js          # Vite build configuration
├── tailwind.config.js      # Tailwind CSS configuration
└── src/
    ├── App.jsx             # Main component with routing setup
    ├── main.jsx            # Application entry point
    ├── index.css           # Global styles and Tailwind directives
    ├── components/         # Reusable React components
    │   ├── auth/           # Authentication-related components (e.g., ProtectedRoute)
    │   ├── layout/         # Site layout components (Navbar, Footer)
    │   └── ui/             # Base UI components from shadcn/ui
    ├── lib/                # Utility functions and API client
    │   └── api.js          # Axios instance and API call definitions
    ├── pages/              # Components for each page/route
    └── store/              # Zustand global state stores
        └── authStore.js    # Authentication state (token, user)
```

### 4.3. State Management

Global state, primarily for authentication, is managed with **Zustand**.

-   **`authStore.js`**:
    -   **State:** `token`, `user`, `isAuthenticated`.
    -   **Actions:** `login`, `logout`, `updateUser`.
    -   **Persistence:** The store is persisted to `localStorage` using Zustand's `persist` middleware. This keeps the user logged in across browser sessions.
    -   The `login` action is asynchronous. It first decodes the JWT to get the user ID, updates the store with the new token, then makes a second API call to `/api/users/:id` to fetch the complete user object.

### 4.4. Routing

-   **Provider:** `react-router-dom`.
-   **Routes:** Defined in `App.jsx`.
-   **Protected Routes:** The `<ProtectedRoute>` component wraps routes that require authentication. It checks for `isAuthenticated` in the `authStore` and verifies the user's role against the `allowedRoles` prop. If the checks fail, it redirects the user to the login page or the homepage.

### 4.5. Styling

-   **Framework:** Tailwind CSS is used for all styling, following a utility-first approach.
-   **Theme:** Colors and other design tokens are defined as CSS variables in `index.css` and consumed by Tailwind's configuration in `tailwind.config.js`. This allows for easy theming (including dark mode).
-   **Components:** Base components are from `shadcn/ui`, which provides unstyled, accessible building blocks.

### 4.6. Configuration & Running

-   **Configuration:** The backend API URL is configured in `frontend/.env` via the `VITE_API_BASE_URL` variable.
-   **Install:** `cd frontend && npm install`
-   **Run (Dev):** `cd frontend && npm run dev` (runs on `http://localhost:5173`)
-   **Build:** `cd frontend && npm run build`
