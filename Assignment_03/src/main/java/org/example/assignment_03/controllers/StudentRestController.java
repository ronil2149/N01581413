package org.example.assignment_03.controllers;

import org.example.assignment_03.models.Course;
import org.example.assignment_03.models.Enrollment;
import org.example.assignment_03.models.User;
import org.example.assignment_03.services.CourseService;
import org.example.assignment_03.services.EnrollmentService;
import org.example.assignment_03.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentRestController {

    private static final Logger logger = LoggerFactory.getLogger(StudentRestController.class);

    @Autowired
    private UserService userService;
    @Autowired private CourseService courseService;
    @Autowired private EnrollmentService enrollmentService;

    private boolean isStudent(String role) {
        return "STUDENT".equalsIgnoreCase(role);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestParam int userId, @RequestParam String userRole) {
        if (!isStudent(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User student = userService.getUserById(userId);
        if (student == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

        return ResponseEntity.ok(student);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> viewProfile(@RequestParam int userId, @RequestParam String userRole) {
        if (!isStudent(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User student = userService.getUserById(userId);
        if (student == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

        return ResponseEntity.ok(student);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser,
                                           @RequestParam int userId,
                                           @RequestParam String userRole) {
        if (!isStudent(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User existing = userService.getUserById(userId);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        updatedUser.setId(userId);
        updatedUser.setPassword(existing.getPassword());
        updatedUser.setRole(existing.getRole());
        updatedUser.setUsername(existing.getUsername());
        updatedUser.setDob(existing.getDob());
        updatedUser.setAccountNonExpired(existing.isAccountNonExpired());
        updatedUser.setAccountNonLocked(existing.isAccountNonLocked());
        updatedUser.setCredentialsNonExpired(existing.isCredentialsNonExpired());
        updatedUser.setEnabled(existing.isEnabled());
        updatedUser.setDeleted(existing.isDeleted());

        User result = userService.updateUser(updatedUser);
        return result != null ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed");
    }

    @GetMapping("/courses")
    public ResponseEntity<?> viewCourses(@RequestParam int userId, @RequestParam String userRole) {
        if (!isStudent(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User student = userService.getUserById(userId);
        List<Course> courses = courseService.getAllActiveCourses();
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);

        Map<String, Object> result = new HashMap<>();
        result.put("courses", courses);
        result.put("enrollments", enrollments);
        result.put("student", student);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/course/enroll")
    public ResponseEntity<?> enrollCourse(@RequestParam int userId,
                                          @RequestParam String userRole,
                                          @RequestParam int courseId) {
        if (!isStudent(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        boolean success = enrollmentService.enrollStudentInCourse(userId, courseId);
        return success ? ResponseEntity.ok("Enrolled successfully")
                : ResponseEntity.status(HttpStatus.CONFLICT).body("Already enrolled or failed");
    }

    @PostMapping("/course/drop")
    public ResponseEntity<?> dropCourse(@RequestParam int userId,
                                        @RequestParam String userRole,
                                        @RequestParam int courseId) {
        if (!isStudent(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        boolean success = enrollmentService.dropCourse(userId, courseId);
        return success ? ResponseEntity.ok("Dropped course successfully")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to drop course");
    }
}
