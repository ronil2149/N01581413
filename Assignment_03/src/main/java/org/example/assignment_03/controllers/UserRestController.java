package org.example.assignment_03.controllers;

import jakarta.validation.Valid;
import org.example.assignment_03.models.User;
import org.example.assignment_03.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addNewUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) return ResponseEntity.badRequest().body(result.getAllErrors());

        try {
            logger.info("Registering user: {}", user.getUsername());
            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username or email already exists.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestParam String username,
                                       @RequestParam String password) {
        try {
            User user = userService.userLogin(username, password);
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("userId", user.getId());
                response.put("userRole", user.getRole());
                response.put("user", user);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }
}
