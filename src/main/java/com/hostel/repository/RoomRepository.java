package com.hostel.repository;

import com.hostel.model.Room;
import com.hostel.model.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHostelId(Long hostelId);

    @Query("SELECT r FROM Room r WHERE r.currentOccupancy < r.capacity AND r.available = true")
    List<Room> findAvailableRooms();

    @Query("SELECT r FROM Room r WHERE r.hostel.id = :hostelId AND r.currentOccupancy < r.capacity AND r.available = true")
    List<Room> findAvailableRoomsByHostel(@Param("hostelId") Long hostelId);

    List<Room> findByRoomType(RoomType roomType);

    Optional<Room> findByRoomNumberAndHostelId(String roomNumber, Long hostelId);
}
