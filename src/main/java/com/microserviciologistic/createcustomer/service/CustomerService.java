package com.microserviciologistic.createcustomer.service;

import com.microserviciologistic.createcustomer.model.Customer;
import com.microserviciologistic.createcustomer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final WebSocketClientService webSocketClientService;

    @Value("${URL_WEBHOOK}")
    private String webhookUrl;
    private final String notificationServiceUrl = webhookUrl+"/notify"; // Microservicio de notificación
    private final String checkNotificationUrl = webhookUrl+"/check_notification?customer_id="; // Verificación previa

    @Autowired
    public CustomerService(CustomerRepository customerRepository, WebSocketClientService webSocketClientService, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
        this.webSocketClientService = webSocketClientService;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer createCustomer(Customer customer) {
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        try {
            //  Encrypt password before saving
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            System.out.println("Save user on database: " + customer);
            Customer createdCustomer = customerRepository.save(customer);
            //EVENT WEBSOCKET
            System.out.println("Enviando evento WebSocket para creación de cliente...");
            webSocketClientService.sendEvent("CREATE", createdCustomer);
            // **We check if the notification has already been sent before sending it**
            if (!checkIfNotificationExists(createdCustomer.getId())) {
                sendNotification(createdCustomer);
            } else {
                System.out.println("Client verified, not sent again.");
            }

            return createdCustomer;
        } catch (DataAccessException e) {
            System.err.println(" Error to save user: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    " Error connecting to the database",
                    e
            );
        }
    }

    /**
     * Checks if a notification already exists in MongoDB before sending it.
     */
    private boolean checkIfNotificationExists(UUID customerId) {
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(checkNotificationUrl + customerId, Boolean.class);
            return response.getBody() != null && response.getBody();
        } catch (Exception e) {
            System.err.println("Error verified notification onn MongoDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends a notification to the notification microservice.
     */
    private void sendNotification(Customer customer) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Building the JSON to send the notification
            String jsonBody = String.format(
                    "{ \"customer_id\": \"%s\", \"name\": \"%s\", \"lastname\": \"%s\", \"email\": \"%s\", \"phone\": \"%s\", \"message\": \"Bienvenido a nuestro servicio\" }",
                    customer.getId(), customer.getName(), customer.getLastname(), customer.getEmail(), customer.getPhone()
            );

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(notificationServiceUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Notification sent correctly.");
            } else {
                System.err.println("Error at sent notification on webhook:" + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error at sent notification on webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

