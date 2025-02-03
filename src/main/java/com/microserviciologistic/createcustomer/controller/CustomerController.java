package com.microserviciologistic.createcustomer.controller;

import com.microserviciologistic.createcustomer.model.Customer;
import com.microserviciologistic.createcustomer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customers") // Base de todos los endpoints
@Tag(name = "Customers", description = "Endpoints for managing customers")
public class CustomerController {

    private final CustomerService customerService;
    private final RestTemplate restTemplate;

    private final String notificationServiceUrl = "http://localhost:5000/notify"; // URL del microservicio de notificaciones

    @Autowired
    public CustomerController(CustomerService customerService, RestTemplate restTemplate) {
        this.customerService = customerService;
        this.restTemplate = restTemplate;
    }

    // Endpoint to CREATE a client and send notification
    @PostMapping
    @Operation(summary = "Create customer", description = "Endpoint to create customers and send notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created and notification sent successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            System.out.println("📌 Recibiendo solicitud para crear un nuevo cliente.");
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (Exception e) {
            System.err.println("Error creando el cliente: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error creando el cliente: " + e.getMessage(), e
            );
        }
    }
}
