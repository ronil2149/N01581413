package org.example.assignment_01.controller;

import org.example.assignment_01.model.Student;
import org.example.assignment_01.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Display all students
    @GetMapping("/")
    public String showAllStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students-list";
    }

    // Show form for adding a new student
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        return "add-student";
    }

    // Handle form submission for adding a student
    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student student) {
        studentService.addStudent(student);
        return "redirect:/";
    }

    // Show form for editing a student
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student != null) {
            model.addAttribute("student", student);
            return "edit-student";
        }
        return "redirect:/";
    }

    // Handle form submission for editing a student
    @PostMapping("/edit/{id}")
    public String editStudent(@PathVariable Long id, @ModelAttribute Student student) {
        student.setId(id);  // Ensure the ID is set
        studentService.updateStudent(student);
        return "redirect:/";
    }

    // Handle student deletion
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/";
    }
}