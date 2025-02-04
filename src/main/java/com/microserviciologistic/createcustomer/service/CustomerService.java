package com.microserviciologistic.createcustomer.service;

import com.microserviciologistic.createcustomer.model.Customer;
import com.microserviciologistic.createcustomer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String notificationServiceUrl = "http://100.26.98.16:5000/notify"; // Microservicio de notificación
    private final String checkNotificationUrl = "http://100.26.98.16:5000/check_notification?customer_id="; // Verificación previa

    @Autowired
    public CustomerService(CustomerRepository customerRepository, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer createCustomer(Customer customer) {
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new IllegalArgumentException("⚠️ Email no puede estar vacío.");
        }

        try {
            //  Encrypt password before saving
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            System.out.println("🟢 Guardando cliente en la base de datos: " + customer);
            Customer createdCustomer = customerRepository.save(customer);

            // **We check if the notification has already been sent before sending it**
            if (!checkIfNotificationExists(createdCustomer.getId())) {
                sendNotification(createdCustomer);
            } else {
                System.out.println("⚠️ Cliente ya notificado previamente, no se enviará otra vez.");
            }

            return createdCustomer;
        } catch (DataAccessException e) {
            System.err.println("❌ Error al guardar usuario: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "❌ Error al conectar con la base de datos",
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
            System.err.println("⚠️ Error verificando notificación en MongoDB: " + e.getMessage());
            return false; // En caso de error, asumimos que no existe y procedemos con el envío
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
                System.out.println("✅ Notificación enviada correctamente.");
            } else {
                System.err.println("⚠️ Error al enviar notificación: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

