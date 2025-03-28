package org.example.assignment_03.repository;

import org.example.assignment_03.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    // Find all active courses
    List<Course> findByIsActiveTrue();

    // Find course by course code
    Course findByCourseCode(String courseCode);
}