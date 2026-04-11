package com.hostel.controller;

import com.hostel.model.Hostel;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.model.enums.Role;
import com.hostel.model.enums.RoomType;
import com.hostel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin Controller handles system-wide management:
 * - Managing hostels and rooms
 * - Managing users (students, wardens)
 * - Viewing all allocations and payments
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final HostelService hostelService;
    private final RoomService roomService;
    private final AllocationService allocationService;
    private final PaymentService paymentService;
    private final ComplaintService complaintService;

    @Autowired
    public AdminController(UserService userService, HostelService hostelService,
                            RoomService roomService, AllocationService allocationService,
                            PaymentService paymentService, ComplaintService complaintService) {
        this.userService = userService;
        this.hostelService = hostelService;
        this.roomService = roomService;
        this.allocationService = allocationService;
        this.paymentService = paymentService;
        this.complaintService = complaintService;
    }

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ─── Dashboard ───────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("totalStudents", userService.findByRole(Role.STUDENT).size());
        model.addAttribute("totalWardens", userService.findByRole(Role.WARDEN).size());
        model.addAttribute("totalRooms", roomService.getAllRooms().size());
        model.addAttribute("totalHostels", hostelService.getAllHostels().size());
        model.addAttribute("totalAllocations", allocationService.getAllAllocations().size());
        model.addAttribute("totalPayments", paymentService.getAllPayments().size());
        model.addAttribute("totalComplaints", complaintService.getAllComplaints().size());
        return "admin/dashboard";
    }

    // ─── User Management ─────────────────────────────────
    @GetMapping("/users")
    public String viewUsers(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("students", userService.findByRole(Role.STUDENT));
        model.addAttribute("wardens", userService.findByRole(Role.WARDEN));
        model.addAttribute("admins", userService.findByRole(Role.ADMIN));
        return "admin/users";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ─── Hostel Management ───────────────────────────────
    @GetMapping("/hostels")
    public String viewHostels(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("hostels", hostelService.getAllHostels());
        model.addAttribute("wardens", userService.findByRole(Role.WARDEN));
        return "admin/hostels";
    }

    @PostMapping("/hostel/add")
    public String addHostel(@RequestParam String name,
                             @RequestParam String address,
                             @RequestParam int totalRooms,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) Long wardenId,
                             RedirectAttributes redirectAttributes) {
        try {
            Hostel hostel = new Hostel(name, address, totalRooms, description);
            if (wardenId != null) {
                User warden = userService.findById(wardenId)
                        .orElseThrow(() -> new RuntimeException("Warden not found"));
                hostel.setWarden(warden);
            }
            hostelService.createHostel(hostel);
            redirectAttributes.addFlashAttribute("successMessage", "Hostel added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/hostels";
    }

    // ─── Room Management ─────────────────────────────────
    @GetMapping("/rooms")
    public String viewRooms(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("hostels", hostelService.getAllHostels());
        model.addAttribute("roomTypes", RoomType.values());
        return "admin/rooms";
    }

    @PostMapping("/room/add")
    public String addRoom(@RequestParam String roomNumber,
                           @RequestParam String roomType,
                           @RequestParam int capacity,
                           @RequestParam double feePerSemester,
                           @RequestParam int floorNumber,
                           @RequestParam Long hostelId,
                           RedirectAttributes redirectAttributes) {
        try {
            Hostel hostel = hostelService.findById(hostelId)
                    .orElseThrow(() -> new RuntimeException("Hostel not found"));
            Room room = new Room(roomNumber, RoomType.valueOf(roomType), capacity,
                    feePerSemester, floorNumber, hostel);
            roomService.createRoom(room);
            redirectAttributes.addFlashAttribute("successMessage", "Room added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/room/delete/{id}")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    // ─── Allocations Overview ─────────────────────────────
    @GetMapping("/allocations")
    public String viewAllocations(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("allocations", allocationService.getAllAllocations());
        return "admin/allocations";
    }

    // ─── Payments Overview ────────────────────────────────
    @GetMapping("/payments")
    public String viewPayments(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("payments", paymentService.getAllPayments());
        return "admin/payments";
    }

    // ─── Complaints Overview ──────────────────────────────
    @GetMapping("/complaints")
    public String viewComplaints(Authentication auth, Model model) {
        User admin = getCurrentUser(auth);
        model.addAttribute("user", admin);
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "admin/complaints";
    }
}
