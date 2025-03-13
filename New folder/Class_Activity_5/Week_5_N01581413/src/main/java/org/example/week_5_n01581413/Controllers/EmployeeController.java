package org.example.week_5_n01581413.Controllers;

import org.example.week_5_n01581413.Services.EmployeeService;
import org.example.week_5_n01581413.models.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // DISPLAY ALL EMPLOYEES
    @GetMapping
    public String getAllEmployees(Model model) {
        model.addAttribute("employeeList", employeeService.getAllEmployees());
        return "employee-list";  // Return the list view
    }

    // DISPLAY FORM TO ADD A NEW EMPLOYEE
    @GetMapping("/add")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-add";  // Return the add form view
    }

    // ADD A NEW EMPLOYEE (FORM SUBMISSION)
    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee) {
        employeeService.addEmployee(employee);
        return "redirect:/employees";  // Redirect to list after adding
    }

    // SHOW EMPLOYEE DETAILS
    @GetMapping("/{id}")
    public String getEmployeeDetails(@PathVariable int id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "employee-details";  // Return employee details view
    }

    // SHOW UPDATE FORM FOR EMPLOYEE
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "employee-edit";  // Return the edit form view
    }

    // HANDLE UPDATE FORM SUBMISSION
    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable int id, @ModelAttribute Employee employee) {
        employeeService.updateEmployee(id, employee);
        return "redirect:/employees";  // Redirect to list after updating
    }

    // DELETE AN EMPLOYEE ITEM
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";  // Redirect to list after deletion
    }
}
