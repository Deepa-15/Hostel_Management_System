package com.hostel.pattern.factory;

import com.hostel.model.User;
import com.hostel.model.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserFactory (Factory Pattern).
 * Tests that the factory correctly creates users with different roles.
 */
class UserFactoryTest {

    @Test
    @DisplayName("Factory should create a STUDENT user correctly")
    void createUser_withStudentRole_shouldSetAllFields() {
        User user = UserFactory.createUser("student1", "pass123", "John Doe",
                "john@example.com", "1234567890", Role.STUDENT);

        assertNotNull(user);
        assertEquals("student1", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("John Doe", user.getFullName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals(Role.STUDENT, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Factory should create a WARDEN user correctly")
    void createUser_withWardenRole_shouldSetAllFields() {
        User user = UserFactory.createUser("warden1", "pass123", "Jane Smith",
                "jane@example.com", "0987654321", Role.WARDEN);

        assertNotNull(user);
        assertEquals(Role.WARDEN, user.getRole());
        assertEquals("warden1", user.getUsername());
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Factory should create an ADMIN user correctly")
    void createUser_withAdminRole_shouldSetAllFields() {
        User user = UserFactory.createUser("admin1", "pass123", "Admin User",
                "admin@example.com", "1111111111", Role.ADMIN);

        assertNotNull(user);
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Convenience method createStudent should set STUDENT role")
    void createStudent_shouldCreateUserWithStudentRole() {
        User user = UserFactory.createStudent("stu1", "pass", "Student One",
                "stu1@example.com", "1234567890");

        assertEquals(Role.STUDENT, user.getRole());
        assertEquals("stu1", user.getUsername());
    }

    @Test
    @DisplayName("Convenience method createWarden should set WARDEN role")
    void createWarden_shouldCreateUserWithWardenRole() {
        User user = UserFactory.createWarden("war1", "pass", "Warden One",
                "war1@example.com", "0987654321");

        assertEquals(Role.WARDEN, user.getRole());
    }

    @Test
    @DisplayName("Convenience method createAdmin should set ADMIN role")
    void createAdmin_shouldCreateUserWithAdminRole() {
        User user = UserFactory.createAdmin("adm1", "pass", "Admin One",
                "adm1@example.com", "1111111111");

        assertEquals(Role.ADMIN, user.getRole());
    }
}
