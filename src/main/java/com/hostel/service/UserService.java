package com.hostel.service;

import com.hostel.model.User;
import com.hostel.model.enums.Role;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User operations.
 * 
 * DESIGN PRINCIPLE - Dependency Inversion Principle (DIP):
 * Controllers depend on this interface, not on the concrete implementation.
 * This allows swapping implementations without changing controller code.
 */
public interface UserService {
    User registerUser(String username, String password, String fullName,
                      String email, String phone, Role role);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findByRole(Role role);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
