package org.example.class_activity_9.services;

import org.example.class_activity_9.models.User;
import org.example.class_activity_9.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User addUser(User user) {
        logger.info("Adding a new user: {}", user.getUsername());

        user.setCreatedDate(LocalDate.now());
        user.setModifiedDate(LocalDate.now());
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password

        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User updateUserById(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            user.setDob(userDetails.getDob());
            user.setModifiedDate(LocalDate.now());
            return userRepository.save(user);
        }).orElse(null);
    }

    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public User findByUsername(String username) {
        List<User> userList = userRepository.findByUsername(username);
        return userList.isEmpty() ? null : userList.get(0);
    }

    public User userLogin(String username, String password) {
        logger.info("Fetching user by name from the database: {}", username);
        List<User> userList = userRepository.findByUsername(username);
        return userList.stream()
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .findFirst()
                .orElse(null);
    }
}