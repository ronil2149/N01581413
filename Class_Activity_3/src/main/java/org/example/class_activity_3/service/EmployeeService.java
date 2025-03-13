package org.example.class_activity_3.service;

import org.example.class_activity_3.model.Employee;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
}