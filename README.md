# REST API in Java with Spring Boot

This project is a simple REST API created with Spring Boot that allows managing the CUSTOMER domain, specifically for the CREATE microservice. The API offers the basic operation such as creating a new user, displaying the SWAGGER documentation technology screen as the main page.
## Project Structure

- **`CreateCustomerApplication.java`**: The main class that runs the Spring Boot application and defines the API controller.

- `POSTT /api/customers/create`: Allows you to create the customer, under the required columns.

## Requirements

- **JDK 17** o superior.
- **Maven** (for dependency management and project construction).

## Installation

1. **Clone the repository**

    ```bash
    git clone <https://github.com/kevinseya/microservicio-logistic-create-customer.git>
    ```

2. **Build and run the application** with Maven:

    ```bash
    mvn spring-boot:run
    ```

3. The application run on: `http://localhost:8080`.

## Use of endpoint

### 1. POST /api/customers/create

Create a new customer. The request body must contain the user details in JSON format.
POST request example:
```bash
POST /api/customers/create Content-Type: application/json
    
    { 
    "name": "John", "lastname": "Doe",
    "email": "john.doe@example.com",
    "phone": "1234567890",
    "password": "securePassword123",
    "address": "Gonzalo Hidalgo y Gualberto Perez S9-50" 
    }
```
**Response:**
```plaintext
    {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "name": "John",
        "lastname": "Doe",
        "email": "john.doe@example.com",
        "phone": "1234567890",
        "rol": "ADMIN",
        "address": "Gonzalo Hidalgo y Gualberto Perez S9-50" 
    }
```
**Response code:**
- **`201 Created:`** Customer created successfully.
- **`500 Internal Server Error:`** Server error.
