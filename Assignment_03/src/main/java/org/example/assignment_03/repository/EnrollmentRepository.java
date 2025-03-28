package org.example.assignment_03.repository;

import org.example.assignment_03.models.Course;
import org.example.assignment_03.models.Enrollment;
import org.example.assignment_03.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    // Find all enrollments for a student
    List<Enrollment> findByStudentAndIsActiveTrue(User student);

    // Find enrollment by student and course
    Enrollment findByStudentAndCourse(User student, Course course);

    // Find all active enrollments for a course
    List<Enrollment> findByCourseAndIsActiveTrue(Course course);

    @Query("SELECT e FROM Enrollment e ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findAllEnrollments();

}