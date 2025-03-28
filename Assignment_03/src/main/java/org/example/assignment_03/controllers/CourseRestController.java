package org.example.assignment_03.controllers;

import org.example.assignment_03.models.Course;
import org.example.assignment_03.models.User;
import org.example.assignment_03.services.CourseService;
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
@RequestMapping("/api/courses")
public class CourseRestController {

    private static final Logger logger = LoggerFactory.getLogger(CourseRestController.class);

    @Autowired private CourseService courseService;
    @Autowired private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCourses(@RequestParam int userId, @RequestParam String userRole) {
        User user = userService.getUserById(userId);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        List<Course> courses = courseService.getAllActiveCourses();

        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("user", user);
        response.put("isAdmin", "ADMIN".equalsIgnoreCase(userRole));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id,
                                           @RequestParam int userId,
                                           @RequestParam String userRole) {
        User user = userService.getUserById(userId);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        Course course = courseService.getCourseById(id);
        if (course == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");

        Map<String, Object> response = new HashMap<>();
        response.put("course", course);
        response.put("user", user);
        response.put("view", "ADMIN".equalsIgnoreCase(userRole) ? "admin" : "student");

        return ResponseEntity.ok(response);
    }
}
