package com.homeservice.controller;

import com.homeservice.entity.*;
import com.homeservice.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class HomeController {

    private final BookingService bookingService;

    public HomeController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("categories", bookingService.getAllCategories());
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        if (user.getRole() == User.Role.EXPERT) {
            return "redirect:/expert/dashboard";
        }

        java.util.List<Booking> bookings = bookingService.getBookingsByUser(user);

        long totalBookings = bookings.size();
        long pendingBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        long completedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelledBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("categories", bookingService.getAllCategories());
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("cancelledBookings", cancelledBookings);
        model.addAttribute("hasBookings", !bookings.isEmpty());
        return "user/dashboard";
    }

    @GetMapping("/book/{categoryId}")
    public String bookingForm(@PathVariable Long categoryId, Model model) {
        ServiceCategory category = bookingService.getCategoryById(categoryId);
        model.addAttribute("category", category);
        return "user/book";
    }

    @PostMapping("/book/{categoryId}")
    public String createBooking(@PathVariable Long categoryId,
                                @RequestParam String address,
                                @RequestParam String bookingDate,
                                @RequestParam String bookingTime,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false, defaultValue = "0") double latitude,
                                @RequestParam(required = false, defaultValue = "0") double longitude,
                                @RequestParam(required = false) Long selectedExpertId,
                                Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        ServiceCategory category = bookingService.getCategoryById(categoryId);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCategory(category);
        booking.setAddress(address);
        booking.setBookingDate(LocalDate.parse(bookingDate));
        booking.setBookingTime(LocalTime.parse(bookingTime));
        booking.setDescription(description);
        booking.setLatitude(latitude);
        booking.setLongitude(longitude);
        booking.setStatus(Booking.BookingStatus.PENDING);

        if (selectedExpertId != null) {
            Expert expert = bookingService.getExpertById(selectedExpertId);
            if (expert != null) {
                booking.setExpert(expert);
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
            }
        }

        booking.setAmount(500.0);
        Booking saved = bookingService.createBooking(booking);
        return "redirect:/payment/" + saved.getId();
    }

    @GetMapping("/payment/{bookingId}")
    public String paymentPage(@PathVariable Long bookingId, Model model, Authentication auth) {
        Booking booking = bookingService.getBookingById(bookingId);
        User user = bookingService.getUserByUsername(auth.getName());
        if (booking == null || !booking.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("booking", booking);
        return "user/payment";
    }

    @PostMapping("/payment/{bookingId}/cash")
    public String payCash(@PathVariable Long bookingId, Authentication auth) {
        Booking booking = bookingService.getBookingById(bookingId);
        User user = bookingService.getUserByUsername(auth.getName());
        if (booking != null && booking.getUser().getId().equals(user.getId())) {
            booking.setPaymentStatus(Booking.PaymentStatus.UNPAID);
            booking.setPaymentId("CASH_ON_SERVICE");
            bookingService.createBooking(booking);
        }
        return "redirect:/dashboard";
    }
}
