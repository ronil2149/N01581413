package org.example.class_activity_4.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Employee {
    private int id; // Auto-generated

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 5, max = 20, message = "First name must be 5-20 characters")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    private String emailId;
}