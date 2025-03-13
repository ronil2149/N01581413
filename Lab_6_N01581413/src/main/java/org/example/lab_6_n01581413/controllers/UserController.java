package org.example.lab_6_n01581413.controllers;

import jakarta.validation.Valid;
import org.example.lab_6_n01581413.models.User;
import org.example.lab_6_n01581413.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHomePage() {
        return "index";
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

            // Check for duplicate entry
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
                return "home";
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