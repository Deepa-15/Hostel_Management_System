package com.hostel.service.impl;

import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Room;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.RoomRepository;
import com.hostel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAvailableRoomsByHostel(Long hostelId) {
        return roomRepository.findAvailableRoomsByHostel(hostelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHostel(Long hostelId) {
        return roomRepository.findByHostelId(hostelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByType(RoomType type) {
        return roomRepository.findByRoomType(type);
    }

    @Override
    public Room updateRoom(Room room) {
        if (!roomRepository.existsById(room.getId())) {
            throw new ResourceNotFoundException("Room", room.getId());
        }
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room", id);
        }
        roomRepository.deleteById(id);
    }
}
