package org.example.assignment_02.controllers;

import org.example.assignment_02.models.Course;
import org.example.assignment_02.models.User;
import org.example.assignment_02.services.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    @GetMapping("/all")
    public String getAllCourses(@ModelAttribute("loggedInUser") User user, Model model) {
        if (user == null) {
            logger.warn("Unauthorized access attempt to courses");
            return "redirect:/";
        }

        logger.info("Loading all courses for user: {}", user.getUsername());
        List<Course> courses = courseService.getAllActiveCourses();
        logger.info("Found {} active courses", courses.size());

        model.addAttribute("courses", courses);
        model.addAttribute("user", user);

        // Determine if admin or student view
        boolean isAdmin = "ADMIN".equals(user.getRole());
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            model.addAttribute("newCourse", new Course()); // For new course form
            return "shared/courses";
        } else {
            return "student/courses";
        }
    }

    @GetMapping("/{id}")
    public String getCourseById(@PathVariable("id") int courseId,
                                @ModelAttribute("loggedInUser") User user,
                                Model model) {
        if (user == null) {
            logger.warn("Unauthorized access attempt to course details");
            return "redirect:/";
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "redirect:/courses/all";
        }

        logger.info("User: {} viewing course: {}", user.getUsername(), course.getCourseCode());
        model.addAttribute("course", course);
        model.addAttribute("user", user);

        // Determine if admin view
        if ("ADMIN".equals(user.getRole())) {
            model.addAttribute("admin", user);
            return "admin/course-details";
        } else {
            model.addAttribute("student", user);
            return "student/course-details"; // You'd need to create this view
        }
    }
}