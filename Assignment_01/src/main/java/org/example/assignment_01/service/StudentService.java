package org.example.assignment_01.service;

import org.example.assignment_01.model.Student;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Constructor to initialize with 3 default records
    public StudentService() {
        // Adding three default students
        addStudent(new Student(null, "John Doe", 20, "Male", "john@example.com", "Toronto", LocalDate.of(2003, 5, 15)));
        addStudent(new Student(null, "Jane Smith", 22, "Female", "jane@example.com", "Vancouver", LocalDate.of(2001, 8, 25)));
        addStudent(new Student(null, "Mike Johnson", 21, "Male", "mike@example.com", "Montreal", LocalDate.of(2002, 3, 10)));
    }

    // Add a new student
    public Student addStudent(Student student) {
        student.setId(idGenerator.getAndIncrement());
        students.put(student.getId(), student);
        return student;
    }

    // Get all students
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    // Get student by ID
    public Student getStudentById(Long id) {
        return students.get(id);
    }

    // Update student
    public Student updateStudent(Student student) {
        if (student.getId() != null && students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    // Delete student
    public void deleteStudent(Long id) {
        students.remove(id);
    }
}