package com.CampusCart.Campus_backend.controller;

import com.CampusCart.Campus_backend.models.Admin;
import com.CampusCart.Campus_backend.models.Report;
import com.CampusCart.Campus_backend.models.User;
import com.CampusCart.Campus_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500") // Allow frontend access
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Get all admins
    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    // Find admin by email
    @GetMapping("/find")
    public Optional<Admin> findAdminByEmail(@RequestParam String email) {
        return adminService.findByEmail(email);
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    // Delete a user by ID (admin-only action)
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return "User with ID " + userId + " has been deleted.";
    }

    // Get total users
    @GetMapping("/totalUsers")
    public long getTotalUsers() {
        return adminService.getTotalUsers();
    }

    // Get all reported users
    @GetMapping("/reportedUsers")
    public List<Report> getReportedUsers() {
        return adminService.getReportedUsers();
    }
}
