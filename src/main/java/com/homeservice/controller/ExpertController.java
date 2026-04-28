package com.homeservice.controller;

import com.homeservice.entity.*;
import com.homeservice.service.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/expert")
public class ExpertController {

    private final BookingService bookingService;

    public ExpertController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);

        if (expert == null) {
            model.addAttribute("user", user);
            model.addAttribute("noProfile", true);
            model.addAttribute("categories", bookingService.getAllCategories());
            return "expert/dashboard";
        }

        List<Booking> myBookings = bookingService.getBookingsByExpert(expert);
        List<Booking> pendingRequests = bookingService.getPendingRequestsForSkills(expert.getSkills());

        long completed = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelled = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();
        long pending = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();

        model.addAttribute("user", user);
        model.addAttribute("expert", expert);
        model.addAttribute("myBookings", myBookings);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("notificationCount", pendingRequests.size());
        model.addAttribute("completedCount", completed);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("totalCount", myBookings.size());
        model.addAttribute("hasBookings", !myBookings.isEmpty());
        model.addAttribute("categories", bookingService.getAllCategories());
        model.addAttribute("selectedSkillIds", expert.getSkills().stream()
                .map(ServiceCategory::getId).collect(Collectors.toSet()));
        return "expert/dashboard";
    }

    @PostMapping("/skills/update")
    public String updateSkills(@RequestParam(required = false) List<Long> skillIds, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);
        if (expert != null) {
            Set<ServiceCategory> skills = new java.util.HashSet<>();
            if (skillIds != null) {
                for (Long id : skillIds) {
                    ServiceCategory cat = bookingService.getCategoryById(id);
                    if (cat != null) skills.add(cat);
                }
            }
            expert.setSkills(skills);
            if (expert.getCategory() == null && !skills.isEmpty()) {
                expert.setCategory(skills.iterator().next());
            }
            bookingService.saveExpert(expert);
        }
        return "redirect:/expert/dashboard";
    }

    @PostMapping("/skills/add/{categoryId}")
    public String addSkill(@PathVariable Long categoryId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);
        if (expert != null) {
            ServiceCategory cat = bookingService.getCategoryById(categoryId);
            if (cat != null) {
                expert.getSkills().add(cat);
                if (expert.getCategory() == null) expert.setCategory(cat);
                bookingService.saveExpert(expert);
            }
        }
        return "redirect:/expert/dashboard";
    }

    @PostMapping("/skills/remove/{categoryId}")
    public String removeSkill(@PathVariable Long categoryId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);
        if (expert != null) {
            expert.getSkills().removeIf(s -> s.getId().equals(categoryId));
            bookingService.saveExpert(expert);
        }
        return "redirect:/expert/dashboard";
    }

    @PostMapping("/accept/{bookingId}")
    public String acceptBooking(@PathVariable Long bookingId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && expert != null && booking.getExpert() == null) {
            booking.setExpert(expert);
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingService.createBooking(booking);
        }
        return "redirect:/expert/dashboard";
    }

    @PostMapping("/reject/{bookingId}")
    public String rejectBooking(@PathVariable Long bookingId) {
        return "redirect:/expert/dashboard";
    }

    @PostMapping("/complete/{bookingId}")
    public String completeBooking(@PathVariable Long bookingId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && expert != null && booking.getExpert() != null
                && booking.getExpert().getId().equals(expert.getId())) {
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            bookingService.createBooking(booking);
        }
        return "redirect:/expert/dashboard";
    }

    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId, Authentication auth) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);
        Booking booking = bookingService.getBookingById(bookingId);

        if (booking != null && expert != null && booking.getExpert() != null
                && booking.getExpert().getId().equals(expert.getId())) {
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            booking.setExpert(null);
            bookingService.createBooking(booking);
        }
        return "redirect:/expert/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = bookingService.getUserByUsername(auth.getName());
        Expert expert = bookingService.getExpertByUser(user);

        if (expert == null) {
            model.addAttribute("user", user);
            return "expert/profile";
        }

        List<Booking> myBookings = bookingService.getBookingsByExpert(expert);

        long completed = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
        long cancelled = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count();
        long pending = myBookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count();

        model.addAttribute("user", user);
        model.addAttribute("expert", expert);
        model.addAttribute("completedCount", completed);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("totalCount", myBookings.size());
        return "expert/profile";
    }
}
