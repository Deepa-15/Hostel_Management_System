package com.hostel.controller;

import com.hostel.model.User;
import com.hostel.model.enums.ComplaintStatus;
import com.hostel.model.enums.PaymentMethod;
import com.hostel.pattern.facade.HostelManagementFacade;
import com.hostel.service.AllocationService;
import com.hostel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Student Controller handles all student-specific operations.
 * 
 * DESIGN PATTERN - Facade Pattern:
 * Uses HostelManagementFacade to simplify interactions with
 * multiple subsystems (Room, Allocation, Payment, Complaint).
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    private final HostelManagementFacade facade;
    private final UserService userService;
    private final AllocationService allocationService;

    @Autowired
    public StudentController(HostelManagementFacade facade,
                              UserService userService,
                              AllocationService allocationService) {
        this.facade = facade;
        this.userService = userService;
        this.allocationService = allocationService;
    }

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ─── Dashboard ───────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        model.addAttribute("allocations", facade.getStudentAllocations(student.getId()));
        model.addAttribute("complaints", facade.getStudentComplaints(student.getId()));
        model.addAttribute("payments", facade.getStudentPayments(student.getId()));
        model.addAttribute("hasActiveAllocation", allocationService.hasActiveAllocation(student.getId()));
        return "student/dashboard";
    }

    // ─── View Available Rooms ─────────────────────────────
    @GetMapping("/rooms")
    public String viewRooms(Model model, Authentication auth) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        model.addAttribute("rooms", facade.getAvailableRooms());
        model.addAttribute("hasActiveAllocation", allocationService.hasActiveAllocation(student.getId()));
        return "student/rooms";
    }

    // ─── Apply for Room ──────────────────────────────────
    @PostMapping("/apply-room")
    public String applyForRoom(@RequestParam Long roomId,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            User student = getCurrentUser(auth);
            facade.applyForRoom(student, roomId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Room application submitted successfully! Awaiting warden approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    // ─── View Allocations ─────────────────────────────────
    @GetMapping("/allocations")
    public String viewAllocations(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        model.addAttribute("allocations", facade.getStudentAllocations(student.getId()));
        return "student/allocations";
    }

    // ─── Make Payment ─────────────────────────────────────
    @GetMapping("/payment/{allocationId}")
    public String showPaymentPage(@PathVariable Long allocationId, Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        model.addAttribute("allocation", allocationService.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Allocation not found")));
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "student/payment";
    }

    @PostMapping("/payment")
    public String makePayment(@RequestParam Long allocationId,
                               @RequestParam String paymentMethod,
                               Authentication auth,
                               RedirectAttributes redirectAttributes) {
        try {
            User student = getCurrentUser(auth);
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod);
            facade.makePayment(student, allocationId, method);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Payment successful! Your room is now confirmed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    // ─── Payment History ─────────────────────────────────
    @GetMapping("/payments")
    public String viewPayments(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        model.addAttribute("payments", facade.getStudentPayments(student.getId()));
        return "student/payments";
    }

    // ─── Submit Complaint ─────────────────────────────────
    @GetMapping("/complaint/new")
    public String showComplaintForm(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        return "student/complaint-form";
    }

    @PostMapping("/complaint")
    public String submitComplaint(@RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam(required = false) String category,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        try {
            User student = getCurrentUser(auth);
            facade.submitComplaint(student, title, description, category);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Complaint submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/complaints";
    }

    // ─── View Complaints ─────────────────────────────────
    @GetMapping("/complaints")
    public String viewComplaints(Authentication auth, Model model) {
        User student = getCurrentUser(auth);
        model.addAttribute("user", student);
        model.addAttribute("complaints", facade.getStudentComplaints(student.getId()));
        return "student/complaints";
    }

    // ─── Vacate Room ──────────────────────────────────────
    @PostMapping("/vacate/{allocationId}")
    public String vacateRoom(@PathVariable Long allocationId,
                              RedirectAttributes redirectAttributes) {
        try {
            facade.vacateRoom(allocationId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Room vacated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}
