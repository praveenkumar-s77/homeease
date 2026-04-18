package com.homeservice.controller;

import com.homeservice.entity.*;
import com.homeservice.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/worker")
public class WorkerController {

    private final BookingService bookingService;

    public WorkerController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        Worker worker = bookingService.getWorkerByUser(user);

        if (worker == null) {
            model.addAttribute("error", "Worker profile not found.");
            return "worker/dashboard";
        }

        List<Booking> myBookings = bookingService.getBookingsByWorker(worker);
        List<Booking> pendingRequests = bookingService.getPendingRequestsForCategory(worker.getCategory());

        long completed = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelled = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();
        long pending = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();

        model.addAttribute("user", user);
        model.addAttribute("worker", worker);
        model.addAttribute("myBookings", myBookings);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("notificationCount", pendingRequests.size());
        model.addAttribute("completedCount", completed);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("totalCount", myBookings.size());
        model.addAttribute("hasBookings", !myBookings.isEmpty());
        model.addAttribute("categories", bookingService.getAllCategories());
        return "worker/dashboard";
    }

    @PostMapping("/accept/{bookingId}")
    public String acceptBooking(@PathVariable Long bookingId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Worker worker = bookingService.getWorkerByUser(user);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && worker != null && booking.getWorker() == null) {
            booking.setWorker(worker);
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingService.createBooking(booking);
        }
        return "redirect:/worker/dashboard";
    }

    @PostMapping("/reject/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId) {
        // Worker just doesn't accept — booking stays pending for others
        return "redirect:/worker/dashboard";
    }

    @PostMapping("/complete/{bookingId}")
    public String completeBooking(@PathVariable Long bookingId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Worker worker = bookingService.getWorkerByUser(user);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && worker != null && booking.getWorker() != null
                && booking.getWorker().getId().equals(worker.getId())) {
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            bookingService.createBooking(booking);
        }
        return "redirect:/worker/dashboard";
    }

    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Worker worker = bookingService.getWorkerByUser(user);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && worker != null && booking.getWorker() != null
                && booking.getWorker().getId().equals(worker.getId())) {
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            booking.setWorker(null);
            bookingService.createBooking(booking);
        }
        return "redirect:/worker/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        Worker worker = bookingService.getWorkerByUser(user);
        List<Booking> myBookings = bookingService.getBookingsByWorker(worker);

        long completed = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelled = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();
        long pending = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();

        model.addAttribute("user", user);
        model.addAttribute("worker", worker);
        model.addAttribute("completedCount", completed);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("totalCount", myBookings.size());
        return "worker/profile";
    }
}
