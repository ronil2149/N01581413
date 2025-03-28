package org.example.assignment_03.controllers;

import jakarta.validation.Valid;
import org.example.assignment_03.models.Course;
import org.example.assignment_03.models.Enrollment;
import org.example.assignment_03.models.User;
import org.example.assignment_03.services.CourseService;
import org.example.assignment_03.services.EnrollmentService;
import org.example.assignment_03.services.UserService;
import org.example.assignment_03.validation.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private static final Logger logger = LoggerFactory.getLogger(AdminRestController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    // Helper to validate ADMIN access
    private boolean isAdmin(String role) {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestParam int userId, @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User admin = userService.getUserById(userId);
        if (admin == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");

        Map<String, Object> response = new HashMap<>();
        response.put("admin", admin);
        response.put("studentCount", userService.getAllActiveStudents().size());
        response.put("courseCount", courseService.getAllActiveCourses().size());
        response.put("enrollments", enrollmentService.getAllEnrollments());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents(@RequestParam int userId, @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        List<User> students = userService.getAllActiveStudents();
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        Map<String, Object> result = new HashMap<>();
        result.put("students", students);
        result.put("enrollments", enrollments);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudent(@PathVariable int id, @RequestParam int userId, @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User student = userService.getUserById(id);
        if (student == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

        return ResponseEntity.ok(student);
    }

    @PutMapping("/student")
    public ResponseEntity<?> updateStudent(@Validated(ValidationGroups.OnUpdate.class) @RequestBody User updatedStudent,
                                           BindingResult bindingResult,
                                           @RequestParam int userId,
                                           @RequestParam String userRole) {

        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        User existing = userService.getUserById(updatedStudent.getId());
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");

        if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(bindingResult.getAllErrors());

        updatedStudent.setPassword(existing.getPassword());
        updatedStudent.setRole(existing.getRole());
        updatedStudent.setUsername(existing.getUsername());
        updatedStudent.setDob(existing.getDob());
        updatedStudent.setAccountNonExpired(existing.isAccountNonExpired());
        updatedStudent.setAccountNonLocked(existing.isAccountNonLocked());
        updatedStudent.setCredentialsNonExpired(existing.isCredentialsNonExpired());
        updatedStudent.setEnabled(existing.isEnabled());
        updatedStudent.setDeleted(existing.isDeleted());
        updatedStudent.setCreatedDate(existing.getCreatedDate());
        updatedStudent.setModifiedDate(LocalDate.now());

        User saved = userService.updateUser(updatedStudent);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable int id,
                                           @RequestParam int userId,
                                           @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        userService.softDeleteUser(id);
        return ResponseEntity.ok("Student deleted successfully");
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(@RequestParam int userId, @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        Map<String, Object> result = new HashMap<>();
        result.put("courses", courseService.getAllActiveCourses());
        result.put("enrollments", enrollmentService.getAllEnrollments());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/course")
    public ResponseEntity<?> addCourse(@Valid @RequestBody Course course,
                                       BindingResult bindingResult,
                                       @RequestParam int userId,
                                       @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(bindingResult.getAllErrors());

        Course added = courseService.addCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @GetMapping("/course/{id}")
    public ResponseEntity<?> getCourse(@PathVariable int id,
                                       @RequestParam int userId,
                                       @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        return ResponseEntity.ok(course);
    }

    @PutMapping("/course")
    public ResponseEntity<?> updateCourse(@Valid @RequestBody Course course,
                                          BindingResult bindingResult,
                                          @RequestParam int userId,
                                          @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(bindingResult.getAllErrors());

        Course updated = courseService.updateCourse(course);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/course/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable int id,
                                          @RequestParam int userId,
                                          @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        courseService.softDeleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }

    @GetMapping("/enrollments")
    public ResponseEntity<?> getAllEnrollments(@RequestParam int userId, @RequestParam String userRole) {
        if (!isAdmin(userRole)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");

        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }
}
