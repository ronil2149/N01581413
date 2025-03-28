package org.example.assignment_03.services;

import org.example.assignment_03.models.Course;
import org.example.assignment_03.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Get all active courses - this method is used by both admin and student views
     * to ensure they see the same course data
     */
    public List<Course> getAllActiveCourses() {
        logger.info("Fetching all active courses");
        List<Course> courses = courseRepository.findByIsActiveTrue();
        logger.info("Found {} active courses", courses.size());
        return courses;
    }

    public Course getCourseById(int id) {
        logger.info("Fetching course with ID: {}", id);
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    public Course addCourse(Course course) {
        logger.info("Adding a new course: {}", course.getCourseCode());

        course.setCreatedDate(LocalDate.now());
        course.setModifiedDate(LocalDate.now());
        course.setActive(true);

        Course savedCourse = courseRepository.save(course);
        logger.info("Course added successfully: {}", savedCourse.getCourseName());
        return savedCourse;
    }

    public Course updateCourse(Course course) {
        logger.info("Updating course: {}", course.getCourseCode());

        Optional<Course> optionalExistingCourse = courseRepository.findById(course.getId());

        if (optionalExistingCourse.isPresent()) {
            Course existingCourse = optionalExistingCourse.get();

            existingCourse.setCourseName(course.getCourseName());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setCredits(course.getCredits());
            existingCourse.setModifiedDate(LocalDate.now());

            Course updatedCourse = courseRepository.save(existingCourse);
            logger.info("Course updated successfully: {}", updatedCourse.getCourseName());
            return updatedCourse;
        } else {
            logger.error("Course not found for update: ID {}", course.getId());
            return null;
        }
    }

    public void softDeleteCourse(int id) {
        logger.info("Soft deleting course with ID: {}", id);

        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setActive(false);
            course.setModifiedDate(LocalDate.now());
            courseRepository.save(course);
            logger.info("Course with ID: {} has been soft deleted", id);
        } else {
            logger.warn("Course with ID: {} not found for deletion", id);
        }
    }
}