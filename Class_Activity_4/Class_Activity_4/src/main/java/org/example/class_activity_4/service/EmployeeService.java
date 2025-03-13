package org.example.class_activity_4.service;

import org.example.class_activity_4.model.Employee;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {
    private final Map<Integer, Employee> employeeMap = new HashMap<>();
    private int nextId = 1;

    public <List> ArrayList<Employee> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public void addEmployee(Employee employee) {
        employee.setId(nextId);
        employeeMap.put(nextId, employee);
        nextId++;
    }

    public Employee getEmployeeById(int id) {
        return employeeMap.get(id);
    }

    public void updateEmployee(Employee employee) {
        employeeMap.put(employee.getId(), employee);
    }

    public void deleteEmployee(int id) {
        employeeMap.remove(id);
    }

    public Optional<Employee> searchEmployee(int id) {
        return Optional.ofNullable(employeeMap.get(id));
    }
}