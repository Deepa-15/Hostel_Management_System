package com.hostel.pattern.factory;

import com.hostel.model.User;
import com.hostel.model.enums.Role;

/**
 * DESIGN PATTERN - Factory Pattern (Creational):
 * 
 * WHY: The Factory Pattern is used here to centralize User creation logic.
 * Instead of scattering user creation with role-specific initialization
 * across multiple controllers/services, the UserFactory provides a single
 * point of creation. This makes it easy to add new roles or modify the
 * creation process without changing client code.
 * 
 * WHERE: Used in UserService during registration to create properly
 * initialized User objects based on the requested role.
 * 
 * DESIGN PRINCIPLE - Open/Closed Principle (OCP):
 * New user types can be added by extending this factory without
 * modifying existing creation logic.
 */
public class UserFactory {

    /**
     * Creates a User with the specified role and default settings.
     *
     * @param username User's login name
     * @param password Encoded password
     * @param fullName Full name of the user
     * @param email    Email address
     * @param phone    Phone number
     * @param role     Role to assign
     * @return Fully initialized User object
     */
    public static User createUser(String username, String password, String fullName,
                                   String email, String phone, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);

        // Apply role-specific defaults
        switch (role) {
            case STUDENT:
                user.setEnabled(true);
                break;
            case WARDEN:
                user.setEnabled(true);
                break;
            case ADMIN:
                user.setEnabled(true);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        return user;
    }

    /**
     * Convenience method to create a Student user.
     */
    public static User createStudent(String username, String password, String fullName,
                                      String email, String phone) {
        return createUser(username, password, fullName, email, phone, Role.STUDENT);
    }

    /**
     * Convenience method to create a Warden user.
     */
    public static User createWarden(String username, String password, String fullName,
                                     String email, String phone) {
        return createUser(username, password, fullName, email, phone, Role.WARDEN);
    }

    /**
     * Convenience method to create an Admin user.
     */
    public static User createAdmin(String username, String password, String fullName,
                                    String email, String phone) {
        return createUser(username, password, fullName, email, phone, Role.ADMIN);
    }
}
