package org.example.assignment_02.controllers;

import jakarta.validation.Valid;
import org.example.assignment_02.models.Course;
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

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    // Admin dashboard
    @GetMapping("/dashboard")
    public String dashboard(@ModelAttribute("loggedInUser") User admin, Model model) {
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            logger.warn("Unauthorized access attempt to admin dashboard");
            return "redirect:/";
        }

        logger.info("Loading dashboard for admin: {}", admin.getUsername());
        model.addAttribute("admin", admin);

        // Get all students for quick overview
        List<User> students = userService.getAllActiveStudents();
        model.addAttribute("studentCount", students.size());

        // Get all courses
        List<Course> courses = courseService.getAllActiveCourses();
        model.addAttribute("courseCount", courses.size());

        return "admin/dashboard";
    }

    // Manage students
    @GetMapping("/students")
    public String manageStudents(@RequestParam(value = "adminId", required = false) Integer adminId, Model model) {
        // If adminId is provided, verify admin
        if (adminId != null) {
            User admin = userService.getUserById(adminId);
            if (admin == null || !"ADMIN".equals(admin.getRole())) {
                return "redirect:/";
            }
            model.addAttribute("admin", admin);
        }

        logger.info("Loading student management page");
        List<User> students = userService.getAllActiveStudents();
        model.addAttribute("students", students);

        return "admin/students";
    }

    // View/edit student details
    @GetMapping("/student/{id}")
    public String viewStudent(@PathVariable("id") int studentId,
                              @RequestParam("adminId") int adminId,
                              Model model) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        User student = userService.getUserById(studentId);
        if (student == null) {
            return "redirect:/admin/students?adminId=" + adminId;
        }

        logger.info("Admin: {} viewing student: {}", admin.getUsername(), student.getUsername());
        model.addAttribute("student", student);
        model.addAttribute("admin", admin);

        return "admin/student-details";
    }

    // Update student
    @PostMapping("/student/update")
    public String updateStudent(@Valid @ModelAttribute User student, BindingResult bindingResult,
                                @RequestParam("adminId") int adminId,
                                Model model, RedirectAttributes redirectAttributes) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("admin", admin);
            return "admin/student-details";
        }

        logger.info("Admin: {} updating student: {}", admin.getUsername(), student.getUsername());
        User updatedStudent = userService.updateUser(student);

        if (updatedStudent != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update student.");
        }

        return "redirect:/admin/student/" + student.getId() + "?adminId=" + adminId;
    }

    // Delete (soft) student
    @PostMapping("/student/delete")
    public String deleteStudent(@RequestParam("studentId") int studentId,
                                @RequestParam("adminId") int adminId,
                                RedirectAttributes redirectAttributes) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        User student = userService.getUserById(studentId);
        if (student == null) {
            return "redirect:/admin/students?adminId=" + adminId;
        }

        logger.info("Admin: {} deleting student: {}", admin.getUsername(), student.getUsername());
        userService.softDeleteUser(studentId);

        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");

        return "redirect:/admin/students?adminId=" + adminId;
    }

    // Manage courses - using shared template
    @GetMapping("/courses")
    public String manageCourses(@RequestParam(value = "adminId", required = false) Integer adminId, Model model) {
        // If adminId is provided, verify admin
        if (adminId == null) {
            return "redirect:/";
        }

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        logger.info("Loading course management page for admin: {}", admin.getUsername());
        List<Course> courses = courseService.getAllActiveCourses();
        logger.info("Found {} active courses for admin view", courses.size());

        // Add attributes needed for the shared template
        model.addAttribute("courses", courses);
        model.addAttribute("user", admin);  // Use "user" instead of "admin" for the shared template
        model.addAttribute("isAdmin", true); // Flag to indicate this is admin view
        model.addAttribute("newCourse", new Course()); // For new course form

        return "shared/courses"; // Use the shared courses template
    }

    // Add new course
    @PostMapping("/course/add")
    public String addCourse(@Valid @ModelAttribute("newCourse") Course course, BindingResult bindingResult,
                            @RequestParam("adminId") int adminId,
                            Model model, RedirectAttributes redirectAttributes) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            List<Course> courses = courseService.getAllActiveCourses();
            model.addAttribute("courses", courses);
            model.addAttribute("user", admin);
            model.addAttribute("isAdmin", true);
            return "shared/courses";
        }

        logger.info("Admin: {} adding new course: {}", admin.getUsername(), course.getCourseCode());
        Course addedCourse = courseService.addCourse(course);

        if (addedCourse != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add course.");
        }

        return "redirect:/admin/courses?adminId=" + adminId;
    }

    // View/edit course details
    @GetMapping("/course/{id}")
    public String viewCourse(@PathVariable("id") int courseId,
                             @RequestParam("adminId") int adminId,
                             Model model) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "redirect:/admin/courses?adminId=" + adminId;
        }

        logger.info("Admin: {} viewing course: {}", admin.getUsername(), course.getCourseCode());
        model.addAttribute("course", course);
        model.addAttribute("admin", admin);

        return "admin/course-details";
    }

    // Update course
    @PostMapping("/course/update")
    public String updateCourse(@Valid @ModelAttribute Course course, BindingResult bindingResult,
                               @RequestParam("adminId") int adminId,
                               Model model, RedirectAttributes redirectAttributes) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("admin", admin);
            return "admin/course-details";
        }

        logger.info("Admin: {} updating course: {}", admin.getUsername(), course.getCourseCode());
        Course updatedCourse = courseService.updateCourse(course);

        if (updatedCourse != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update course.");
        }

        return "redirect:/admin/course/" + course.getId() + "?adminId=" + adminId;
    }

    // Delete (soft) course
    @PostMapping("/course/delete")
    public String deleteCourse(@RequestParam("courseId") int courseId,
                               @RequestParam("adminId") int adminId,
                               RedirectAttributes redirectAttributes) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "redirect:/admin/courses?adminId=" + adminId;
        }

        logger.info("Admin: {} deleting course: {}", admin.getUsername(), course.getCourseCode());
        courseService.softDeleteCourse(courseId);

        redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");

        return "redirect:/admin/courses?adminId=" + adminId;
    }

    @GetMapping("/admin/courses")
    public String manageCourses(@RequestParam("adminId") int adminId, Model model) {
        List<Course> courses = courseService.getAllActiveCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("adminId", adminId);  // Preserve adminId
        return "admin/courses";
    }



}