package com.microserviciologistic.createcustomer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    @Schema(description = "Name of the customerr", example = "John", required = true)
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Lastname is required")
    @Schema(description = "Lastname of the customer", example = "Doe", required = true)
    private String lastname;

    @Column(nullable = false)
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the customer", example = "john.doe@example.com", required = true)
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must have 10 digits")
    @Schema(description = "Phone number of the customer", example = "1234567890", required = true)
    private String phone;

    @Column(nullable = false)
    @Size(min = 6, message = "Password must have at least 6 characters")
    @Schema(description = "Password of the customer", example = "securePassword123", required = true)
    private String password;
    @Column(nullable = false)
    @NotBlank(message = "Address is required")
    @Schema(description = "Address of the customer", example = "Gonzalo Hidalgo y Gualberto Perez S9-50", required = true)
    private String address;

    @Column(nullable = false)
    @Schema(description = "Indica si el usuario está activo o inactivo", example = "true")
    private Boolean active = true;

    public UUID getId() { return id; }

    public void setId(UUID id) {
        this.id = id;
    }

    // Getter y Setter para name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter y Setter para lastname
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    // Getter y Setter para email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter y Setter para phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter y Setter para password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) { this.address = address; }

    //Getter y Setter para active
    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }
}
