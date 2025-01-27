package com.microserviciologistic.createcustomer.service;

import com.microserviciologistic.createcustomer.model.Customer;
import com.microserviciologistic.createcustomer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        try {
            System.out.println("Saving customer on database: " + customer);
            return customerRepository.save(customer);
        } catch (DataAccessException e) {
            System.err.println("Error to save customer: " + e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error connecting to the database",
                    e
            );
        }
    }
}
