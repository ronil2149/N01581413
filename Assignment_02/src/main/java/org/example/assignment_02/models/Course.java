package org.example.assignment_02.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Course code is required")
    @Column(unique = true)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100, message = "Course name must be between 3 and 100 characters")
    private String courseName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private int credits;
    private LocalDate createdDate;
    private LocalDate modifiedDate;
    private boolean isActive = true;
}