package com.CampusCart.Campus_backend.controller;

import com.CampusCart.Campus_backend.models.User;
import com.CampusCart.Campus_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500") // Allow frontend access
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register a new user
   @PostMapping("/register")
public User registerUser(@Valid @RequestBody User user) {
    System.out.println("Received user: " + user); // Check null values
    return userService.registerUser(user);
}

    // Login user
    @PostMapping("/login")
    public String loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> user = userService.authenticateUser(email, password);
        if (user.isPresent()) {
            return "Login successful. Welcome, " + user.get().getFirstName() + "!";
        } else {
            return "Invalid email or password.";
        }
    }


    // Get a user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}