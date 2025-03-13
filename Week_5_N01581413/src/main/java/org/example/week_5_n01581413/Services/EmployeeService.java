package org.example.week_5_n01581413.Services;

import org.example.week_5_n01581413.models.Employee;
import org.example.week_5_n01581413.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Constructor-based dependency injection
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Method to get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();  // Fetch all employees from DB
    }

    // Method to add a new employee
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);  // Save employee to DB
    }

    // Method to update an employee
    public Employee updateEmployee(int id, Employee updatedEmployee) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);

        // If employee exists, update and save
        if (existingEmployee.isPresent()) {
            updatedEmployee.setId(id);
            return employeeRepository.save(updatedEmployee);  // Save updated employee
        }
        return null;
    }

    // Method to delete an employee
    public void deleteEmployee(int id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);  // Delete employee by ID from DB
        }
    }

    // Method to get an employee by ID
    public Employee getEmployeeById(int id) {
        return employeeRepository.findById(id).orElse(null);  // Fetch employee by ID, return null if not found
    }
}
