package com.hostel.controller;

import com.hostel.model.User;
import com.hostel.model.enums.AllocationStatus;
import com.hostel.model.enums.ComplaintStatus;
import com.hostel.pattern.facade.HostelManagementFacade;
import com.hostel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Warden Controller handles room allocation approval/rejection and complaint management.
 */
@Controller
@RequestMapping("/warden")
public class WardenController {

    private final HostelManagementFacade facade;
    private final UserService userService;
    private final AllocationService allocationService;
    private final ComplaintService complaintService;
    private final RoomService roomService;

    @Autowired
    public WardenController(HostelManagementFacade facade,
                             UserService userService,
                             AllocationService allocationService,
                             ComplaintService complaintService,
                             RoomService roomService) {
        this.facade = facade;
        this.userService = userService;
        this.allocationService = allocationService;
        this.complaintService = complaintService;
        this.roomService = roomService;
    }

    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ─── Dashboard ───────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User warden = getCurrentUser(auth);
        model.addAttribute("user", warden);
        model.addAttribute("pendingAllocations", facade.getPendingAllocations());
        model.addAttribute("allComplaints", complaintService.getAllComplaints());
        model.addAttribute("allRooms", roomService.getAllRooms());
        return "warden/dashboard";
    }

    // ─── Pending Allocations ─────────────────────────────
    @GetMapping("/allocations")
    public String viewAllocations(Authentication auth, Model model) {
        User warden = getCurrentUser(auth);
        model.addAttribute("user", warden);
        model.addAttribute("pendingAllocations", allocationService.getAllocationsByStatus(AllocationStatus.PENDING));
        model.addAttribute("approvedAllocations", allocationService.getAllocationsByStatus(AllocationStatus.APPROVED));
        model.addAttribute("confirmedAllocations", allocationService.getAllocationsByStatus(AllocationStatus.CONFIRMED));
        return "warden/allocations";
    }

    // ─── Approve Allocation ──────────────────────────────
    @PostMapping("/allocation/approve/{id}")
    public String approveAllocation(@PathVariable Long id,
                                     Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        try {
            User warden = getCurrentUser(auth);
            facade.approveAllocation(id, warden);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Allocation approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/warden/allocations";
    }

    // ─── Reject Allocation ───────────────────────────────
    @PostMapping("/allocation/reject/{id}")
    public String rejectAllocation(@PathVariable Long id,
                                    @RequestParam(required = false) String remarks,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        try {
            User warden = getCurrentUser(auth);
            facade.rejectAllocation(id, warden, remarks != null ? remarks : "Rejected by warden");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Allocation rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/warden/allocations";
    }

    // ─── Vacate Room ─────────────────────────────────────
    @PostMapping("/vacate/{id}")
    public String vacateRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facade.vacateRoom(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Room vacated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/warden/allocations";
    }

    // ─── Complaints ──────────────────────────────────────
    @GetMapping("/complaints")
    public String viewComplaints(Authentication auth, Model model) {
        User warden = getCurrentUser(auth);
        model.addAttribute("user", warden);
        model.addAttribute("complaints", complaintService.getAllComplaints());
        return "warden/complaints";
    }

    @PostMapping("/complaint/update/{id}")
    public String updateComplaintStatus(@PathVariable Long id,
                                         @RequestParam String status,
                                         @RequestParam(required = false) String notes,
                                         Authentication auth,
                                         RedirectAttributes redirectAttributes) {
        try {
            User warden = getCurrentUser(auth);
            ComplaintStatus newStatus = ComplaintStatus.valueOf(status);
            complaintService.updateComplaintStatus(id, newStatus, warden, notes);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Complaint status updated to " + newStatus + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/warden/complaints";
    }

    // ─── Room Overview ───────────────────────────────────
    @GetMapping("/rooms")
    public String viewRooms(Authentication auth, Model model) {
        User warden = getCurrentUser(auth);
        model.addAttribute("user", warden);
        model.addAttribute("rooms", roomService.getAllRooms());
        return "warden/rooms";
    }
}
