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

import java.time.LocalDate;
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
            return "redirect:/";
        }

        logger.info("Loading dashboard for admin: {}", admin.getUsername());
        model.addAttribute("admin", admin);

        // Fetch students and courses
        List<User> students = userService.getAllActiveStudents();
        model.addAttribute("studentCount", students.size());

        List<Course> courses = courseService.getAllActiveCourses();
        model.addAttribute("courseCount", courses.size());

        // Fetch enrollments so they stay across all pages
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        model.addAttribute("enrollments", enrollments);

        return "admin/dashboard";
    }

    // Manage students
    @GetMapping("/students")
    public String manageStudents(@RequestParam(value = "adminId", required = false) Integer adminId, Model model) {
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

        // Fetch enrollments so they stay across all pages
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        model.addAttribute("enrollments", enrollments);

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
    public String updateStudent(@Valid @ModelAttribute User updatedStudent, BindingResult bindingResult,
                                @RequestParam("adminId") int adminId,
                                Model model, RedirectAttributes redirectAttributes) {

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        // Fetch the existing student from the database
        User existingStudent = userService.getUserById(updatedStudent.getId());
        if (existingStudent == null) {
            return "redirect:/admin/students?adminId=" + adminId;
        }

        // Preserve fields that should NOT be overwritten
        updatedStudent.setPassword(existingStudent.getPassword()); // Keep the existing password
        updatedStudent.setRole(existingStudent.getRole());
        updatedStudent.setUsername(existingStudent.getUsername());
        updatedStudent.setAccountNonExpired(existingStudent.isAccountNonExpired());
        updatedStudent.setAccountNonLocked(existingStudent.isAccountNonLocked());
        updatedStudent.setCredentialsNonExpired(existingStudent.isCredentialsNonExpired());
        updatedStudent.setEnabled(existingStudent.isEnabled());
        updatedStudent.setDeleted(existingStudent.isDeleted());
        updatedStudent.setCreatedDate(existingStudent.getCreatedDate()); // Preserve original creation date
        updatedStudent.setModifiedDate(LocalDate.now()); // Update modification date

        // If validation errors exist, return to the form
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: " + bindingResult.getAllErrors());
            model.addAttribute("admin", admin);
            model.addAttribute("student", updatedStudent);
            return "admin/student-details";
        }

        // Perform the update
        User savedStudent = userService.updateUser(updatedStudent);
        if (savedStudent != null) {
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update student.");
        }

        return "redirect:/admin/student/" + updatedStudent.getId() + "?adminId=" + adminId;
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
        if (adminId == null) {
            return "redirect:/";
        }

        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        logger.info("Loading course management page for admin: {}", admin.getUsername());
        List<Course> courses = courseService.getAllActiveCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("user", admin);
        model.addAttribute("isAdmin", true);
        model.addAttribute("newCourse", new Course());

        // Fetch enrollments so they stay across all pages
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        model.addAttribute("enrollments", enrollments);

        return "shared/courses";
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
        model.addAttribute("adminId", adminId);
        return "admin/courses";
    }

    @GetMapping("/enrollments")
    public String viewEnrollments(@RequestParam("adminId") int adminId, Model model) {
        User admin = userService.getUserById(adminId);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/";
        }

        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("admin", admin);

        return "admin/enrollments";
    }





}