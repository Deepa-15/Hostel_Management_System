package com.hostel.service;

import com.hostel.model.Room;
import com.hostel.model.enums.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    Room createRoom(Room room);
    Optional<Room> findById(Long id);
    List<Room> getAllRooms();
    List<Room> getAvailableRooms();
    List<Room> getAvailableRoomsByHostel(Long hostelId);
    List<Room> getRoomsByHostel(Long hostelId);
    List<Room> getRoomsByType(RoomType type);
    Room updateRoom(Room room);
    void deleteRoom(Long id);
}
