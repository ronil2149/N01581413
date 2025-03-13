package org.example.assignment_02.repository;

import org.example.assignment_02.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByUsername(String username);

    // Find all non-deleted users with STUDENT role
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.role = 'STUDENT'")
    List<User> findAllActiveStudents();

    // Find all users with ADMIN role
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();

    // Find a user by ID that is not deleted
    @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.isDeleted = false")
    User findActiveById(Integer id);
}