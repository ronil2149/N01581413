package org.example.class_activity_6.services;

import org.example.class_activity_6.models.User;
import org.example.class_activity_6.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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

        userRepository.save(user);
    }

    public User userLogin(String username, String password) {
        logger.info("Fetching user by name from the database: {}", username);

        List<User> userList = userRepository.findByUsername(username);

        return userList.stream()
                .filter(user -> user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}