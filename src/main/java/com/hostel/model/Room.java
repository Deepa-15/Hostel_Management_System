package com.hostel.model;

import com.hostel.model.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Room entity representing a room in a hostel.
 * 
 * DESIGN PRINCIPLE - Single Responsibility Principle (SRP):
 * This class is only responsible for room data. Allocation logic
 * is handled by the AllocationService.
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Positive(message = "Capacity must be positive")
    @Column(nullable = false)
    private int capacity;

    @Column(name = "current_occupancy", nullable = false)
    private int currentOccupancy = 0;

    @Column(name = "fee_per_semester", nullable = false)
    private double feePerSemester;

    @Column(name = "floor_number")
    private int floorNumber;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    // Constructors
    public Room() {}

    public Room(String roomNumber, RoomType roomType, int capacity, double feePerSemester, int floorNumber, Hostel hostel) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.feePerSemester = feePerSemester;
        this.floorNumber = floorNumber;
        this.hostel = hostel;
    }

    /**
     * Check if the room has available beds.
     */
    public boolean hasVacancy() {
        return currentOccupancy < capacity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public double getFeePerSemester() { return feePerSemester; }
    public void setFeePerSemester(double feePerSemester) { this.feePerSemester = feePerSemester; }

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }
}
