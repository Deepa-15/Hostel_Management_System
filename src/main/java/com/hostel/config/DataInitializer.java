package com.hostel.config;

import com.hostel.model.*;
import com.hostel.model.enums.*;
import com.hostel.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds the database with initial data for testing and demonstration.
 * Creates default users, hostels, rooms, allocations, and payments
 * so the system has realistic data on first launch.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, HostelRepository hostelRepo,
                                       RoomRepository roomRepo, AllocationRepository allocationRepo,
                                       PaymentRepository paymentRepo, PasswordEncoder encoder) {
        return args -> {
            // Only seed if the database is empty
            if (userRepo.count() > 0) {
                logger.info("Database already initialized. Skipping data seeding.");
                return;
            }

            logger.info("🌱 Seeding database with initial data...");

            // ── Create Admin ──────────────────────────────────
            User admin = new User("admin", encoder.encode("admin123"), "System Administrator",
                    "admin@hostel.com", "9999999999", Role.ADMIN);
            userRepo.save(admin);

            // ── Create Wardens ────────────────────────────────
            User warden1 = new User("warden1", encoder.encode("warden123"), "Mr. Rajesh Kumar",
                    "warden1@hostel.com", "9888888888", Role.WARDEN);
            User warden2 = new User("warden2", encoder.encode("warden123"), "Mrs. Priya Singh",
                    "warden2@hostel.com", "9777777777", Role.WARDEN);
            userRepo.save(warden1);
            userRepo.save(warden2);

            // ── Create Students ───────────────────────────────
            User student1 = new User("student1", encoder.encode("student123"), "Amit Sharma",
                    "amit@student.com", "9666666666", Role.STUDENT);
            User student2 = new User("student2", encoder.encode("student123"), "Neha Patel",
                    "neha@student.com", "9555555555", Role.STUDENT);
            User student3 = new User("student3", encoder.encode("student123"), "Rahul Verma",
                    "rahul@student.com", "9444444444", Role.STUDENT);
            userRepo.save(student1);
            userRepo.save(student2);
            userRepo.save(student3);

            // ── Create Hostels ────────────────────────────────
            Hostel hostel1 = new Hostel("Nehru Hostel", "Block A, Campus", 20, "Boys hostel with modern amenities");
            hostel1.setWarden(warden1);
            hostelRepo.save(hostel1);

            Hostel hostel2 = new Hostel("Gandhi Hostel", "Block B, Campus", 15, "Girls hostel with premium facilities");
            hostel2.setWarden(warden2);
            hostelRepo.save(hostel2);

            // ── Create Rooms for Hostel 1 ─────────────────────
            Room room101 = roomRepo.save(new Room("101", RoomType.SINGLE, 1, 25000.0, 1, hostel1));
            Room room102 = roomRepo.save(new Room("102", RoomType.SINGLE, 1, 25000.0, 1, hostel1));
            Room room103 = roomRepo.save(new Room("103", RoomType.DOUBLE, 2, 18000.0, 1, hostel1));
            roomRepo.save(new Room("104", RoomType.DOUBLE, 2, 18000.0, 1, hostel1));
            roomRepo.save(new Room("201", RoomType.TRIPLE, 3, 12000.0, 2, hostel1));
            roomRepo.save(new Room("202", RoomType.TRIPLE, 3, 12000.0, 2, hostel1));
            roomRepo.save(new Room("203", RoomType.SINGLE, 1, 28000.0, 2, hostel1));
            roomRepo.save(new Room("204", RoomType.DOUBLE, 2, 20000.0, 2, hostel1));

            // ── Create Rooms for Hostel 2 ─────────────────────
            roomRepo.save(new Room("101", RoomType.SINGLE, 1, 30000.0, 1, hostel2));
            roomRepo.save(new Room("102", RoomType.DOUBLE, 2, 22000.0, 1, hostel2));
            roomRepo.save(new Room("103", RoomType.DOUBLE, 2, 22000.0, 1, hostel2));
            roomRepo.save(new Room("201", RoomType.SINGLE, 1, 32000.0, 2, hostel2));
            roomRepo.save(new Room("202", RoomType.TRIPLE, 3, 15000.0, 2, hostel2));

            // ═══════════════════════════════════════════════════
            // SEED SAMPLE ALLOCATIONS & PAYMENTS
            // ═══════════════════════════════════════════════════

            // ── Student1 (Amit): CONFIRMED allocation + completed payment ──
            Allocation alloc1 = new Allocation(student1, room101);
            alloc1.setStatus(AllocationStatus.CONFIRMED);
            alloc1.setApprovedBy(warden1);
            alloc1.setApprovalDate(LocalDateTime.now().minusDays(5));
            alloc1.setCheckInDate(LocalDate.now().minusDays(4));
            allocationRepo.save(alloc1);

            // Update room occupancy for confirmed allocation
            room101.setCurrentOccupancy(1);
            room101.setAvailable(false); // SINGLE room is now full
            roomRepo.save(room101);

            // Create completed payment for student1
            Payment payment1 = new Payment(student1, alloc1, room101.getFeePerSemester(), PaymentMethod.ONLINE);
            payment1.setTransactionId("ONL-DEMO-" + System.currentTimeMillis());
            payment1.setStatus(PaymentStatus.COMPLETED);
            payment1.setDescription("Hostel fee payment for Room 101 via Online Payment");
            paymentRepo.save(payment1);

            // ── Student2 (Neha): APPROVED allocation (ready to pay) ──
            Allocation alloc2 = new Allocation(student2, room103);
            alloc2.setStatus(AllocationStatus.APPROVED);
            alloc2.setApprovedBy(warden1);
            alloc2.setApprovalDate(LocalDateTime.now().minusDays(1));
            allocationRepo.save(alloc2);

            // ── Student3 (Rahul): PENDING allocation (waiting for warden) ──
            Allocation alloc3 = new Allocation(student3, room102);
            alloc3.setStatus(AllocationStatus.PENDING);
            allocationRepo.save(alloc3);

            logger.info("✅ Database seeded successfully!");
            logger.info("═══════════════════════════════════════");
            logger.info("  DEFAULT LOGIN CREDENTIALS:");
            logger.info("  Admin:   admin / admin123");
            logger.info("  Warden:  warden1 / warden123");
            logger.info("  Student: student1 / student123");
            logger.info("═══════════════════════════════════════");
            logger.info("  DEMO DATA:");
            logger.info("  student1 — CONFIRMED room + payment done");
            logger.info("  student2 — APPROVED (can pay fee now)");
            logger.info("  student3 — PENDING (needs warden approval)");
            logger.info("═══════════════════════════════════════");
        };
    }
}
