package org.example.week_5_n01581413.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Generates getters, seters, toString, equals, and hashCode
@NoArgsConstructor  // Generates a constructor with no arguments
@AllArgsConstructor  // Generates a constructor with all arguments
@Entity  // Marks this class as an entity for JPA
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generate IDs using Identity strategy
    private int id;

    private String firstName;
    private String lastName;
    private String emailId;

    // Lombok annotations will automatically generate constructor, getters, and setters
}