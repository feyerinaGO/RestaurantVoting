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

Swagger UI: http://localhost:8080/swagger-ui.html

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

### Restaurants
- `GET /api/restaurants` - Get all restaurants with today's menu
- `GET /api/restaurants/{id}` - Get restaurant with today's menu
- `GET /api/restaurants/by-date?date={date}` - Get restaurants with menu for specific date

### Votes
- `GET /api/profile/votes` - Get user's voting history
- `GET /api/profile/votes/today` - Get today's vote
- `POST /api/profile/votes?restaurantId={id}` - Vote for restaurant
- `DELETE /api/profile/votes/today` - Delete today's vote (before 11:00)

### Admin - Users
- `GET /api/admin/users` - Get all users
- `GET /api/admin/users/{id}` - Get user by ID
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user
- `PATCH /api/admin/users/{id}?enabled={bool}` - Enable/disable user

### Admin - Restaurants
- `POST /api/restaurants` - Create restaurant
- `PUT /api/restaurants/{id}` - Update restaurant
- `DELETE /api/restaurants/{id}` - Delete restaurant

### Admin - Menu Items
- `GET /api/admin/restaurants/{restaurantId}/menu-items` - Get all menu items for restaurant
- `GET /api/admin/restaurants/{restaurantId}/menu-items/{id}` - Get menu item
- `POST /api/admin/restaurants/{restaurantId}/menu-items` - Create menu item
- `PUT /api/admin/restaurants/{restaurantId}/menu-items/{id}` - Update menu item
- `DELETE /api/admin/restaurants/{restaurantId}/menu-items/{id}` - Delete menu item
- `DELETE /api/admin/restaurants/{restaurantId}/menu-items/by-date?date={date}` - Delete all menu items for date

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

# Login and get restaurants (today's menu)
curl -u user@yandex.ru:password "http://localhost:8080/api/restaurants"

# Vote for restaurant
curl -u user@yandex.ru:password -X POST "http://localhost:8080/api/profile/votes?restaurantId=1"

# Get voting history
curl -u user@yandex.ru:password "http://localhost:8080/api/profile/votes"

# Admin: Create restaurant
curl -u admin@gmail.com:admin -X POST "http://localhost:8080/api/restaurants" \
  -H "Content-Type: application/json" \
  -d '{"name":"New Restaurant","address":"123 Street"}'

# Admin: Add menu item
curl -u admin@gmail.com:admin -X POST "http://localhost:8080/api/admin/restaurants/1/menu-items" \
  -H "Content-Type: application/json" \
  -d '{"name":"Special Dish","menuDate":"2025-01-04","description":"Delicious dish","price":1200}'