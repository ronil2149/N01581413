package org.example.assignment_03.models;

import lombok.Data;
import org.example.assignment_03.models.Course;

@Data
public class CourseWithEnrollmentStatus {
    private Course course;
    private boolean enrolled;

    public CourseWithEnrollmentStatus(Object course, boolean enrolled) {
        this.course = (Course) course;
        this.enrolled = enrolled;
    }
}