package org.example.class_activity_9.controllers;

import jakarta.validation.Valid;
import org.example.class_activity_9.models.User;
import org.example.class_activity_9.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UserMvcController {

    private static final Logger logger = LoggerFactory.getLogger(UserMvcController.class);
    private final UserService userService;

    public UserMvcController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/signup")
    public String addNewUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "add-user";
        }
        try {
            logger.info("Received request to add user: {}", user.getUsername());
            userService.addUser(user);
            model.addAttribute("response", "User registered successfully!");
            return "index";
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());

            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                model.addAttribute("errorMessage", "Username or email already exists. Please choose a different one.");
            } else {
                model.addAttribute("errorMessage", "An error occurred during registration. Please try again.");
            }

            return "add-user";
        }
    }

    @PostMapping("/login")
    public String userLogin(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            logger.info("Received request to login for user: {}", username);
            User user = userService.userLogin(username, password);

            if (!Objects.isNull(user)) {
                logger.info("User {}, logged in successfully", username);
                model.addAttribute("username", username);
                return "home";  // Returns home.html
            } else {
                logger.info("Authentication failed for the user: {}", username);
                model.addAttribute("response", "Invalid Username/Password");
                return "index";
            }
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
            model.addAttribute("response", "An error occurred during login. Please try again.");
            return "index";
        }
    }
}