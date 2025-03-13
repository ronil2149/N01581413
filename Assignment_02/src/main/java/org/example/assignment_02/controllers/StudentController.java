package org.example.assignment_02.controllers;

import jakarta.validation.Valid;
import org.example.assignment_02.models.Course;
import org.example.assignment_02.models.CourseWithEnrollmentStatus;
import org.example.assignment_02.models.Enrollment;
import org.example.assignment_02.models.User;
import org.example.assignment_02.services.CourseService;
import org.example.assignment_02.services.EnrollmentService;
import org.example.assignment_02.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping("/student")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    // Student dashboard
    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("loggedInUser") User student, Model model) {
        if (student == null || student.getId() == 0) {
            logger.warn("Unauthorized access attempt to student dashboard");
            return "redirect:/";
        }

        // Fetch the latest user data
        User updatedStudent = userService.getUserById(student.getId());
        if (updatedStudent == null) {
            return "redirect:/";
        }

        logger.info("Loading dashboard for student: {}", updatedStudent.getUsername());
        model.addAttribute("student", updatedStudent);

        return "student/dashboard";
    }

    // View profile
    @GetMapping("/profile")
    public String viewProfile(@RequestParam("id") int studentId, Model model) {
        User student = userService.getUserById(studentId);

        if (student == null) {
            logger.warn("Unauthorized access attempt to student profile");
            return "redirect:/";
        }

        logger.info("Loading profile for student: {}", student.getUsername());
        model.addAttribute("student", student);

        return "student/profile";
    }

    // Update profile - Fixed version
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        logger.info("Attempting to update profile for student ID: {}", updatedUser.getId());

        // Validation check
        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors in profile update for student: {}", updatedUser.getUsername());
            model.addAttribute("student", updatedUser);
            return "student/profile";
        }

        // Get the existing user
        User existingUser = userService.getUserById(updatedUser.getId());
        if (existingUser == null) {
            logger.error("Cannot find user with ID: {}", updatedUser.getId());
            return "redirect:/";
        }

        // Preserve fields that shouldn't be updated through this form
        updatedUser.setPassword(existingUser.getPassword());
        updatedUser.setRole(existingUser.getRole());
        updatedUser.setUsername(existingUser.getUsername());
        updatedUser.setAccountNonExpired(existingUser.isAccountNonExpired());
        updatedUser.setAccountNonLocked(existingUser.isAccountNonLocked());
        updatedUser.setCredentialsNonExpired(existingUser.isCredentialsNonExpired());
        updatedUser.setEnabled(existingUser.isEnabled());
        updatedUser.setDeleted(existingUser.isDeleted());

        // Debug logging
                User result = userService.updateUser(updatedUser);

        if (result != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            redirectAttributes.addFlashAttribute("loggedInUser", result);
            logger.info("Profile updated successfully for student: {}", result.getUsername());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile. Please try again.");
            logger.error("Profile update failed for student: {}", existingUser.getUsername());
        }

        return "redirect:/student/dashboard";
    }

    // View available courses - Using the shared template correctly
    @GetMapping("/courses")
    public String viewCourses(@RequestParam("id") int studentId, Model model) {
        User student = userService.getUserById(studentId);

        if (student == null) {
            logger.warn("Unauthorized access attempt to courses view");
            return "redirect:/";
        }

        logger.info("Loading courses for student: {}", student.getUsername());

        // Get all required data
        List<Course> courses = courseService.getAllActiveCourses();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);

        logger.info("Found {} active courses and {} enrollments for student",
                courses.size(), enrollments != null ? enrollments.size() : 0);

        // Add all required attributes to the model
        model.addAttribute("courses", courses);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("student", student);  // For backward compatibility
        model.addAttribute("user", student);     // For shared template
        model.addAttribute("isAdmin", false);    // For shared template

        // Important: Add an empty newCourse for form handling, even though students don't use it
        // This prevents null pointer exceptions in the shared template
        model.addAttribute("newCourse", new Course());

        return "shared/courses"; // Use the shared template
    }

    // Enroll in a course
    @PostMapping("/course/enroll")
    public String enrollCourse(@RequestParam("studentId") int studentId,
                               @RequestParam("courseId") int courseId,
                               RedirectAttributes redirectAttributes) {

        User student = userService.getUserById(studentId);
        if (student == null) {
            logger.warn("Unauthorized course enrollment attempt");
            return "redirect:/";
        }

        boolean success = enrollmentService.enrollStudentInCourse(studentId, courseId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in course.");
            logger.info("Student: {} successfully enrolled in course ID: {}", student.getUsername(), courseId);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to enroll in course. You may already be enrolled.");
            logger.warn("Failed enrollment attempt for student: {} in course ID: {}", student.getUsername(), courseId);
        }

        redirectAttributes.addFlashAttribute("loggedInUser", student);
        return "redirect:/student/courses?id=" + studentId;
    }

    // Drop a course
    @PostMapping("/course/drop")
    public String dropCourse(@RequestParam("studentId") int studentId,
                             @RequestParam("courseId") int courseId,
                             RedirectAttributes redirectAttributes) {

        User student = userService.getUserById(studentId);
        if (student == null) {
            logger.warn("Unauthorized course drop attempt");
            return "redirect:/";
        }

        boolean success = enrollmentService.dropCourse(studentId, courseId);

        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully dropped the course.");
            logger.info("Student: {} successfully dropped course ID: {}", student.getUsername(), courseId);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to drop the course.");
            logger.warn("Failed course drop attempt for student: {} in course ID: {}", student.getUsername(), courseId);
        }

        redirectAttributes.addFlashAttribute("loggedInUser", student);
        return "redirect:/student/courses?id=" + studentId;
    }
}