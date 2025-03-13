package org.example.assignment_02.models;

import lombok.Data;

@Data
public class CourseWithEnrollmentStatus {
    private Course course;
    private boolean enrolled;

    public CourseWithEnrollmentStatus(Object course, boolean enrolled) {
        this.course = (Course) course;
        this.enrolled = enrolled;
    }
}