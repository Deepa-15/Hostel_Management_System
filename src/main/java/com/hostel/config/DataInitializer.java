package com.hostel.config;

import com.hostel.model.Hostel;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.model.enums.Role;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds the database with initial data for testing.
 * Creates default admin, warden, students, hostels, and rooms.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, HostelRepository hostelRepo,
                                       RoomRepository roomRepo, PasswordEncoder encoder) {
        return args -> {
            // Only seed if the database is empty
            if (userRepo.count() > 0) {
                logger.info("Database already initialized. Skipping data seeding.");
                return;
            }

            logger.info("🌱 Seeding database with initial data...");

            // Create Admin
            User admin = new User("admin", encoder.encode("admin123"), "System Administrator",
                    "admin@hostel.com", "9999999999", Role.ADMIN);
            userRepo.save(admin);

            // Create Wardens
            User warden1 = new User("warden1", encoder.encode("warden123"), "Mr. Rajesh Kumar",
                    "warden1@hostel.com", "9888888888", Role.WARDEN);
            User warden2 = new User("warden2", encoder.encode("warden123"), "Mrs. Priya Singh",
                    "warden2@hostel.com", "9777777777", Role.WARDEN);
            userRepo.save(warden1);
            userRepo.save(warden2);

            // Create Students
            User student1 = new User("student1", encoder.encode("student123"), "Amit Sharma",
                    "amit@student.com", "9666666666", Role.STUDENT);
            User student2 = new User("student2", encoder.encode("student123"), "Neha Patel",
                    "neha@student.com", "9555555555", Role.STUDENT);
            User student3 = new User("student3", encoder.encode("student123"), "Rahul Verma",
                    "rahul@student.com", "9444444444", Role.STUDENT);
            userRepo.save(student1);
            userRepo.save(student2);
            userRepo.save(student3);

            // Create Hostels
            Hostel hostel1 = new Hostel("Nehru Hostel", "Block A, Campus", 20, "Boys hostel with modern amenities");
            hostel1.setWarden(warden1);
            hostelRepo.save(hostel1);

            Hostel hostel2 = new Hostel("Gandhi Hostel", "Block B, Campus", 15, "Girls hostel with premium facilities");
            hostel2.setWarden(warden2);
            hostelRepo.save(hostel2);

            // Create Rooms for Hostel 1
            roomRepo.save(new Room("101", RoomType.SINGLE, 1, 25000.0, 1, hostel1));
            roomRepo.save(new Room("102", RoomType.SINGLE, 1, 25000.0, 1, hostel1));
            roomRepo.save(new Room("103", RoomType.DOUBLE, 2, 18000.0, 1, hostel1));
            roomRepo.save(new Room("104", RoomType.DOUBLE, 2, 18000.0, 1, hostel1));
            roomRepo.save(new Room("201", RoomType.TRIPLE, 3, 12000.0, 2, hostel1));
            roomRepo.save(new Room("202", RoomType.TRIPLE, 3, 12000.0, 2, hostel1));
            roomRepo.save(new Room("203", RoomType.SINGLE, 1, 28000.0, 2, hostel1));
            roomRepo.save(new Room("204", RoomType.DOUBLE, 2, 20000.0, 2, hostel1));

            // Create Rooms for Hostel 2
            roomRepo.save(new Room("101", RoomType.SINGLE, 1, 30000.0, 1, hostel2));
            roomRepo.save(new Room("102", RoomType.DOUBLE, 2, 22000.0, 1, hostel2));
            roomRepo.save(new Room("103", RoomType.DOUBLE, 2, 22000.0, 1, hostel2));
            roomRepo.save(new Room("201", RoomType.SINGLE, 1, 32000.0, 2, hostel2));
            roomRepo.save(new Room("202", RoomType.TRIPLE, 3, 15000.0, 2, hostel2));

            logger.info("✅ Database seeded successfully!");
            logger.info("═══════════════════════════════════════");
            logger.info("  DEFAULT LOGIN CREDENTIALS:");
            logger.info("  Admin:   admin / admin123");
            logger.info("  Warden:  warden1 / warden123");
            logger.info("  Student: student1 / student123");
            logger.info("═══════════════════════════════════════");
        };
    }
}
