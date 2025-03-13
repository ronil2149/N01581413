package org.example.class_activity_6.controllers;

import org.example.class_activity_6.models.User;
import org.example.class_activity_6.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String addNewUser(@ModelAttribute User user, Model model) {
        logger.info("Received request to add user: {}", user.getUsername());
        userService.addUser(user);
        model.addAttribute("response", "User registered successfully!");
        return "index";
    }

    @PostMapping("/login")
    public String userLogin(@RequestParam String username, @RequestParam String password, Model model) {
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
    }
}