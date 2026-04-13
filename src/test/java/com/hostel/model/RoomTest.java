package com.hostel.model;

import com.hostel.model.enums.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Room entity.
 * Tests the hasVacancy() business logic method.
 */
class RoomTest {

    @Test
    @DisplayName("Room should have vacancy when occupancy is below capacity")
    void hasVacancy_belowCapacity_returnsTrue() {
        Room room = new Room("A101", RoomType.DOUBLE, 2, 5000.0, 1, null);
        room.setCurrentOccupancy(1);

        assertTrue(room.hasVacancy());
    }

    @Test
    @DisplayName("Room should report no vacancy when occupancy equals capacity")
    void hasVacancy_atCapacity_returnsFalse() {
        Room room = new Room("A101", RoomType.SINGLE, 1, 5000.0, 1, null);
        room.setCurrentOccupancy(1);

        assertFalse(room.hasVacancy());
    }

    @Test
    @DisplayName("Room should have vacancy when occupancy is zero")
    void hasVacancy_emptyRoom_returnsTrue() {
        Room room = new Room("A101", RoomType.TRIPLE, 3, 3000.0, 1, null);
        room.setCurrentOccupancy(0);

        assertTrue(room.hasVacancy());
    }
}
