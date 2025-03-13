package org.example.class_activity_4.controller;

import jakarta.validation.Valid;
import org.example.class_activity_4.model.Employee;
import org.example.class_activity_4.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Get all employees
    @GetMapping("/")
    public String getAllEmployees(Model model) {
        model.addAttribute("employeeList", employeeService.getAllEmployees());
        return "manageEmployee";
    }

    // Show form to add new employee
    @GetMapping("/showAddEmployeeForm")
    public String addNewEmployee(Model model) {
        model.addAttribute("employee", new Employee()); // no-args constructor
        return "addEmployee";
    }

    // Add new employee
    @PostMapping("/addEmployee")
    public String addEmployee(
            @ModelAttribute("employee") @Valid Employee employee,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "addEmployee";
        }
        employeeService.addEmployee(employee);
        return "redirect:/";
    }

    // Show form to update employee
    @GetMapping("/showUpdateForm/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "updateEmployee";
    }

    // Update employee
    @PostMapping("/updateEmployee")
    public String updateEmployee(
            @ModelAttribute("employee") @Valid Employee employee,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "updateEmployee";
        }
        employeeService.updateEmployee(employee);
        return "redirect:/";
    }

    // Delete employee
    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        employeeService.deleteEmployee(id);
        return "redirect:/";
    }

    // Search employee
    @GetMapping("/search")
    public String searchEmployee(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            Optional<Employee> employee = employeeService.searchEmployee(id);
            if (employee.isPresent()) {
                model.addAttribute("searchResult", employee.get());
            } else {
                model.addAttribute("errorMessage", "Employee not found with ID: " + id);
            }
        }
        model.addAttribute("employeeList", employeeService.getAllEmployees());
        return "manageEmployee";
    }
}
