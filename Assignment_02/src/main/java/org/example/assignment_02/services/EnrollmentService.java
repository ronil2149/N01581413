package org.example.assignment_02.services;

import org.example.assignment_02.models.Course;
import org.example.assignment_02.models.Enrollment;
import org.example.assignment_02.models.User;
import org.example.assignment_02.repository.CourseRepository;
import org.example.assignment_02.repository.EnrollmentRepository;
import org.example.assignment_02.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Enrollment> getEnrollmentsByStudent(User student) {
        logger.info("Fetching enrollments for student: {}", student.getUsername());
        List<Enrollment> enrollments = enrollmentRepository.findByStudentAndIsActiveTrue(student);

        // Always return a non-null list, even if empty
        if (enrollments == null) {
            logger.info("No enrollments found for student: {}, returning empty list", student.getUsername());
            return new ArrayList<>();
        }

        logger.info("Found {} enrollments for student: {}", enrollments.size(), student.getUsername());
        return enrollments;
    }

    public boolean enrollStudentInCourse(int studentId, int courseId) {
        logger.info("Enrolling student ID: {} in course ID: {}", studentId, courseId);

        Optional<User> optionalStudent = userRepository.findById(studentId);
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalStudent.isPresent() && optionalCourse.isPresent()) {
            User student = optionalStudent.get();
            Course course = optionalCourse.get();

            // Check if already enrolled
            Enrollment existingEnrollment = enrollmentRepository.findByStudentAndCourse(student, course);
            if (existingEnrollment != null) {
                if (!existingEnrollment.isActive()) {
                    // Reactivate enrollment if it was previously dropped
                    existingEnrollment.setActive(true);
                    existingEnrollment.setModifiedDate(LocalDate.now());
                    enrollmentRepository.save(existingEnrollment);
                    logger.info("Reactivated enrollment for student: {} in course: {}", student.getUsername(), course.getCourseCode());
                    return true;
                }
                logger.warn("Student: {} is already enrolled in course: {}", student.getUsername(), course.getCourseCode());
                return false;
            }

            // Create new enrollment
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setEnrollmentDate(LocalDate.now());
            enrollment.setCreatedDate(LocalDate.now());
            enrollment.setModifiedDate(LocalDate.now());
            enrollment.setActive(true);

            enrollmentRepository.save(enrollment);
            logger.info("Student: {} successfully enrolled in course: {}", student.getUsername(), course.getCourseCode());
            return true;
        } else {
            logger.error("Failed to enroll student. Student ID: {} or Course ID: {} not found", studentId, courseId);
            return false;
        }
    }

    public boolean dropCourse(int studentId, int courseId) {
        logger.info("Dropping course ID: {} for student ID: {}", courseId, studentId);

        Optional<User> optionalStudent = userRepository.findById(studentId);
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalStudent.isPresent() && optionalCourse.isPresent()) {
            User student = optionalStudent.get();
            Course course = optionalCourse.get();

            Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course);
            if (enrollment != null && enrollment.isActive()) {
                enrollment.setActive(false);
                enrollment.setModifiedDate(LocalDate.now());
                enrollmentRepository.save(enrollment);
                logger.info("Student: {} successfully dropped course: {}", student.getUsername(), course.getCourseCode());
                return true;
            } else {
                logger.warn("No active enrollment found for student: {} in course: {}", student.getUsername(), course.getCourseCode());
                return false;
            }
        } else {
            logger.error("Failed to drop course. Student ID: {} or Course ID: {} not found", studentId, courseId);
            return false;
        }
    }

    public List<Enrollment> getEnrollmentsByCourse(Course course) {
        logger.info("Fetching enrollments for course: {}", course.getCourseCode());
        return enrollmentRepository.findByCourseAndIsActiveTrue(course);
    }

    public List<Enrollment> getAllEnrollments() {
        logger.info("Fetching all enrollments");
        return enrollmentRepository.findAll();
    }

}