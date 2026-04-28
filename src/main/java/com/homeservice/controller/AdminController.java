package com.homeservice.controller;

import com.homeservice.entity.*;
import com.homeservice.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookingService bookingService;

    public AdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        java.util.List<Booking> bookings = bookingService.getAllBookings();
        java.util.List<Expert> experts = bookingService.getAllExperts();
        java.util.List<User> users = bookingService.getAllUsers();

        long totalUsers = users.stream().filter(u -> u.getRole() == User.Role.USER).count();
        long totalExperts = users.stream().filter(u -> u.getRole() == User.Role.EXPERT).count();
        long pendingBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        long confirmedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();
        long completedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelledBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();

        model.addAttribute("bookings", bookings);
        model.addAttribute("experts", experts);
        model.addAttribute("users", users);
        model.addAttribute("categories", bookingService.getAllCategories());
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalExperts", totalExperts);
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("confirmedBookings", confirmedBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("cancelledBookings", cancelledBookings);
        return "admin/dashboard";
    }

    // --- Expert Management (Admin Only) ---

    @GetMapping("/workers")
    public String listWorkers(Model model) {
        model.addAttribute("experts", bookingService.getAllExperts());
        model.addAttribute("categories", bookingService.getAllCategories());
        return "admin/workers";
    }

    @GetMapping("/workers/add")
    public String addWorkerForm(Model model) {
        model.addAttribute("expert", new Expert());
        model.addAttribute("categories", bookingService.getAllCategories());
        return "admin/expert-form";
    }

    @PostMapping("/workers/save")
    public String saveExpert(@ModelAttribute Expert expert, @RequestParam Long categoryId) {
        ServiceCategory category = bookingService.getCategoryById(categoryId);
        expert.setCategory(category);
        bookingService.saveExpert(expert);
        return "redirect:/admin/workers";
    }

    @GetMapping("/workers/edit/{id}")
    public String editWorker(@PathVariable Long id, Model model) {
        model.addAttribute("expert", bookingService.getExpertById(id));
        model.addAttribute("categories", bookingService.getAllCategories());
        return "admin/expert-form";
    }

    @GetMapping("/workers/delete/{id}")
    public String deleteExpert(@PathVariable Long id) {
        bookingService.deleteExpert(id);
        return "redirect:/admin/workers";
    }

    // --- Booking Management ---

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/assign")
    public String assignWorker(@PathVariable Long id, @RequestParam Long expertId) {
        Booking booking = bookingService.getBookingById(id);
        Expert expert = bookingService.getExpertById(expertId);
        if (booking != null && expert != null) {
            booking.setExpert(expert);
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingService.createBooking(booking);
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/bookings/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        bookingService.updateBookingStatus(id, Booking.BookingStatus.valueOf(status));
        return "redirect:/admin/bookings";
    }
}
