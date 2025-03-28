package org.example.assignment_03.services;

import org.example.assignment_03.models.User;
import org.example.assignment_03.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public void addUser(User user) {
        logger.info("Adding a new user: {}", user.getUsername());

        user.setCreatedDate(LocalDate.now());
        user.setModifiedDate(LocalDate.now());

        // Setting default account status
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        // Default role is STUDENT, which is already set in the User class

        userRepository.save(user);
        logger.info("User added successfully: {}", user.getUsername());
    }

    public User userLogin(String username, String password) {
        logger.info("Authenticating user: {}", username);

        List<User> userList = userRepository.findByUsername(username);

        User user = userList.stream()
                .filter(u -> !u.isDeleted() && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            logger.info("User authenticated successfully: {}", username);
        } else {
            logger.warn("Authentication failed for user: {}", username);
        }

        return user;
    }

    public List<User> getAllActiveStudents() {
        logger.info("Fetching all active students");
        return userRepository.findAllActiveStudents();
    }

    public User getUserById(int id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findActiveById(id);
    }

    public User updateUser(User user) {
        logger.info("Updating user: {}", user.getUsername());

        try {
            Optional<User> optionalExistingUser = userRepository.findById(user.getId());

            if (optionalExistingUser.isPresent()) {
                User existingUser = optionalExistingUser.get();

                // Only update editable fields
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                existingUser.setEmail(user.getEmail());
                existingUser.setAddress(user.getAddress());
                existingUser.setDob(user.getDob());
                existingUser.setModifiedDate(LocalDate.now()); // Update modification date

                User updatedUser = userRepository.save(existingUser);
                logger.info("User updated successfully: {}", updatedUser.getUsername());
                return updatedUser;
            } else {
                logger.error("User not found for update: ID {}", user.getId());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return null;
        }
    }

    public void softDeleteUser(int id) {
        logger.info("Soft deleting user with ID: {}", id);

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(true);
            user.setModifiedDate(LocalDate.now());
            userRepository.save(user);
            logger.info("User with ID: {} has been soft deleted", id);
        } else {
            logger.warn("User with ID: {} not found for deletion", id);
        }
    }
}