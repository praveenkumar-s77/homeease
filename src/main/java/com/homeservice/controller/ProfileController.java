package com.homeservice.controller;

import com.homeservice.entity.*;
import com.homeservice.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProfileController {

    private final BookingService bookingService;

    public ProfileController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        List<Booking> bookings = bookingService.getBookingsByUser(user);

        long total = bookings.size();
        long pending = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        long completed = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelled = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();

        model.addAttribute("user", user);
        model.addAttribute("totalBookings", total);
        model.addAttribute("pendingBookings", pending);
        model.addAttribute("completedBookings", completed);
        model.addAttribute("cancelledBookings", cancelled);
        model.addAttribute("wishlist", bookingService.getWishlistByUser(user));
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        model.addAttribute("user", user);
        return "user/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String permanentAddress,
                                Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPermanentAddress(permanentAddress);
        bookingService.saveUser(user);

        model.addAttribute("user", user);
        model.addAttribute("success", true);
        return "user/profile-edit";
    }

    // Wishlist endpoints
    @PostMapping("/wishlist/add/{expertId}")
    public String addToWishlist(@PathVariable Long expertId, Authentication auth,
                                @RequestHeader(value = "Referer", required = false) String referer) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertById(expertId);
        if (expert != null) {
            bookingService.addToWishlist(user, expert);
        }
        return "redirect:" + (referer != null ? referer : "/profile");
    }

    @PostMapping("/wishlist/remove/{expertId}")
    public String removeFromWishlist(@PathVariable Long expertId, Authentication auth,
                                     @RequestHeader(value = "Referer", required = false) String referer) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertById(expertId);
        if (expert != null) {
            bookingService.removeFromWishlist(user, expert);
        }
        return "redirect:" + (referer != null ? referer : "/profile");
    }
}
