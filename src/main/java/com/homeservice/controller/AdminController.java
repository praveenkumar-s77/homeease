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
        java.util.List<Worker> workers = bookingService.getAllWorkers();
        java.util.List<User> users = bookingService.getAllUsers();

        long totalUsers = users.stream().filter(u -> u.getRole() == User.Role.USER).count();
        long totalWorkers = users.stream().filter(u -> u.getRole() == User.Role.WORKER).count();
        long pendingBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        long confirmedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();
        long completedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelledBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();

        model.addAttribute("bookings", bookings);
        model.addAttribute("workers", workers);
        model.addAttribute("users", users);
        model.addAttribute("categories", bookingService.getAllCategories());
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalWorkers", totalWorkers);
        model.addAttribute("totalBookings", bookings.size());
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("confirmedBookings", confirmedBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("cancelledBookings", cancelledBookings);
        return "admin/dashboard";
    }

    // --- Worker Management (Admin Only) ---

    @GetMapping("/workers")
    public String listWorkers(Model model) {
        model.addAttribute("workers", bookingService.getAllWorkers());
        model.addAttribute("categories", bookingService.getAllCategories());
        return "admin/workers";
    }

    @GetMapping("/workers/add")
    public String addWorkerForm(Model model) {
        model.addAttribute("worker", new Worker());
        model.addAttribute("categories", bookingService.getAllCategories());
        return "admin/worker-form";
    }

    @PostMapping("/workers/save")
    public String saveWorker(@ModelAttribute Worker worker, @RequestParam Long categoryId) {
        ServiceCategory category = bookingService.getCategoryById(categoryId);
        worker.setCategory(category);
        bookingService.saveWorker(worker);
        return "redirect:/admin/workers";
    }

    @GetMapping("/workers/edit/{id}")
    public String editWorker(@PathVariable Long id, Model model) {
        model.addAttribute("worker", bookingService.getWorkerById(id));
        model.addAttribute("categories", bookingService.getAllCategories());
        return "admin/worker-form";
    }

    @GetMapping("/workers/delete/{id}")
    public String deleteWorker(@PathVariable Long id) {
        bookingService.deleteWorker(id);
        return "redirect:/admin/workers";
    }

    // --- Booking Management ---

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings";
    }

    @PostMapping("/bookings/{id}/assign")
    public String assignWorker(@PathVariable Long id, @RequestParam Long workerId) {
        Booking booking = bookingService.getBookingById(id);
        Worker worker = bookingService.getWorkerById(workerId);
        if (booking != null && worker != null) {
            booking.setWorker(worker);
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
