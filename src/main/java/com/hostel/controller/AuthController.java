package com.hostel.controller;

import com.hostel.model.User;
import com.hostel.model.enums.Role;
import com.hostel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Authentication Controller handles login, registration, and dashboard routing.
 * 
 * DESIGN PRINCIPLE - Single Responsibility Principle (SRP):
 * This controller handles ONLY authentication-related requests.
 * Student, Warden, and Admin operations are in separate controllers.
 */
@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "logout", required = false) String logout,
                                 Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("roles", Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam String role,
                                RedirectAttributes redirectAttributes) {
        try {
            Role userRole = Role.valueOf(role);
            userService.registerUser(username, password, fullName, email, phone, userRole);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Routes authenticated users to their role-specific dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";

        String role = auth.getAuthorities().iterator().next().getAuthority();

        return switch (role) {
            case "ROLE_STUDENT" -> "redirect:/student/dashboard";
            case "ROLE_WARDEN" -> "redirect:/warden/dashboard";
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            default -> "redirect:/login";
        };
    }
}
