package org.example.class_activity_3.controller;

import org.example.class_activity_3.model.Employee;
import org.example.class_activity_3.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("employee", new Employee("", "", ""));
        return "addEmployee";
    }

    // Add new employee
    @PostMapping("/addEmployee")
    public String addEmployee(@ModelAttribute("employee") Employee employee, BindingResult bindingResult) {
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
    public String updateEmployee(@ModelAttribute("employee") Employee employee) {
        employeeService.updateEmployee(employee);
        return "redirect:/";
    }

    // Delete employee
    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        employeeService.deleteEmployee(id);
        return "redirect:/";
    }
}
