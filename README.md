# Restaurant Voting System

A REST API for restaurant voting system where users can vote for their preferred restaurant.

## Technical Requirements

- Two types of users: admin and regular users
- Admin can input a restaurant and its lunch menu of the day (2-5 items, dish name and price)
- Menu changes each day (admins do updates)
- Users can vote for a restaurant they want to have lunch at today
- Only one vote counted per user per day
- If user votes again the same day:
    - Before 11:00: vote can be changed
    - After 11:00: vote cannot be changed

## Technology Stack

- Spring Boot 4.x
- Spring Data JPA
- H2 Database (in-memory)
- Spring Security (HTTP Basic)
- Spring Cache (Caffeine)
- Swagger/OpenAPI 3.0
- Lombok
- JUnit 5

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui/index.html

## Test Credentials

- **User**: user@yandex.ru / password
- **Admin**: admin@gmail.com / admin

## API Endpoints

### Public
- `POST /api/profile` - Register new user

### User Profile (authenticated)
- `GET /api/profile` - Get current user profile
- `PUT /api/profile` - Update current user profile
- `DELETE /api/profile` - Delete current user profile

### Restaurants (authenticated)
- `GET /api/restaurants` - Get all restaurants (without menu)
- `GET /api/restaurants/{id}` - Get restaurant by ID (without menu)
- `GET /api/restaurants/with-menu/today` - Get all restaurants with today's menu
- `GET /api/restaurants/{id}/with-menu/today` - Get restaurant with today's menu
- `GET /api/restaurants/with-menu/by-date?date={date}` - Get restaurants with menu for specific date (format YYYY-MM-DD)

### Votes (authenticated)
- `GET /api/profile/votes` - Get user's voting history
- `GET /api/profile/votes/today` - Get today's vote
- `POST /api/profile/votes` - Vote for restaurant (требуется JSON: `{"restaurantId": id}`)
- `PUT /api/profile/votes/today` - Update today's vote (before 11:00, JSON: `{"restaurantId": id}`)

### Admin - Users
- `GET /api/admin/users` - Get all users
- `GET /api/admin/users/{id}` - Get user by ID
- `GET /api/admin/users/by-email?email={email}` - Get user by email
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user
- `PATCH /api/admin/users/{id}?enabled={bool}` - Enable/disable user

### Admin - Restaurants
- `GET /api/admin/restaurants/{id}` - Get restaurant without menu
- `GET /api/admin/restaurants/{id}/with-menu` - Get full restaurant information with all menu items
- `POST /api/admin/restaurants` - Create restaurant (JSON: `{"name": "...", "address": "..."}`)
- `PUT /api/admin/restaurants/{id}` - Update restaurant
- `DELETE /api/admin/restaurants/{id}` - Delete restaurant

### Admin - Menu Items
- `GET /api/admin/restaurants/{restaurantId}/menu-items` - Get all menu items for restaurant
- `GET /api/admin/restaurants/{restaurantId}/menu-items/{id}` - Get menu item
- `GET /api/admin/restaurants/{restaurantId}/menu-items/by-date?date={date}` - Get menu items by date (format YYYY-MM-DD)
- `POST /api/admin/restaurants/{restaurantId}/menu-items` - Create menu item
- `PUT /api/admin/restaurants/{restaurantId}/menu-items/{id}` - Update menu item
- `DELETE /api/admin/restaurants/{restaurantId}/menu-items/{id}` - Delete menu item
- `DELETE /api/admin/restaurants/{restaurantId}/menu-items/by-date?date={date}` - Delete all menu items for date

## Time Restrictions
- Vote can be changed only before 11:00 
- After 11:00 vote becomes unchangeable

## Running the Application

1. Clone the repository
2. Build with Maven: `mvn clean install`
3. Run: `mvn spring-boot:run`
4. Access at: http://localhost:8080

## Testing with curl

```bash
# Register new user
curl -X POST "http://localhost:8080/api/profile" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@test.com","password":"testpass"}'

# Login and get restaurants (without menu)
curl -u user@yandex.ru:password "http://localhost:8080/api/restaurants"

# Get restaurants with today's menu
curl -u user@yandex.ru:password "http://localhost:8080/api/restaurants/with-menu/today"

# Vote for restaurant 
curl -u user@yandex.ru:password -X POST "http://localhost:8080/api/profile/votes" \
  -H "Content-Type: application/json" \
  -d '{"restaurantId": 1}'

# Get voting history
curl -u user@yandex.ru:password "http://localhost:8080/api/profile/votes"

# Update vote (before 11:00)
curl -u user@yandex.ru:password -X PUT "http://localhost:8080/api/profile/votes/today" \
  -H "Content-Type: application/json" \
  -d '{"restaurantId": 2}'

# Admin: Create restaurant
curl -u admin@gmail.com:admin -X POST "http://localhost:8080/api/admin/restaurants" \
  -H "Content-Type: application/json" \
  -d '{"name":"New Restaurant","address":"123 Street"}'

# Admin: Add menu item
curl -u admin@gmail.com:admin -X POST "http://localhost:8080/api/admin/restaurants/1/menu-items" \
  -H "Content-Type: application/json" \
  -d '{"name":"Special Dish","menuDate":"2025-01-04","description":"Delicious dish","price":1200}'