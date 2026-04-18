package com.homeservice.controller;

import com.homeservice.entity.Worker;
import com.homeservice.service.BookingService;
import com.homeservice.service.LocationService;
import com.homeservice.service.LocationService.NearbyWorker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;
    private final BookingService bookingService;

    public LocationController(LocationService locationService, BookingService bookingService) {
        this.locationService = locationService;
        this.bookingService = bookingService;
    }

    @GetMapping("/nearby-workers")
    public ResponseEntity<List<Map<String, Object>>> getNearbyWorkers(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam Long categoryId,
            @RequestParam(required = false) Double radius) {

        List<NearbyWorker> nearby = locationService.findNearbyWorkers(lat, lng, categoryId, radius);

        List<Map<String, Object>> result = new ArrayList<>();
        for (NearbyWorker nw : nearby) {
            Worker w = nw.getWorker();
            Map<String, Object> map = new HashMap<>();
            map.put("id", w.getId());
            map.put("name", w.getName());
            map.put("rating", w.getRating());
            map.put("hourlyRate", w.getHourlyRate());
            map.put("distanceKm", nw.getDistanceKm());
            map.put("category", w.getCategory().getName());
            map.put("verified", w.isVerified());
            map.put("completedBookings", bookingService.getCompletedBookingsCount(w));
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/worker-profile/{id}")
    public ResponseEntity<Map<String, Object>> getWorkerProfile(@PathVariable Long id) {
        Worker w = bookingService.getWorkerById(id);
        if (w == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", w.getId());
        profile.put("name", w.getName());
        profile.put("rating", w.getRating());
        profile.put("hourlyRate", w.getHourlyRate());
        profile.put("category", w.getCategory().getName());
        profile.put("address", w.getAddress());
        profile.put("available", w.isAvailable());
        profile.put("verified", w.isVerified());
        profile.put("completedBookings", bookingService.getCompletedBookingsCount(w));
        profile.put("totalBookings", bookingService.getTotalBookingsCount(w));
        // Phone is NOT included — hidden from users until they click reveal
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/worker-phone/{id}")
    public ResponseEntity<Map<String, String>> getWorkerPhone(@PathVariable Long id) {
        Worker w = bookingService.getWorkerById(id);
        if (w == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("phone", w.getPhone() != null ? w.getPhone() : ""));
    }
}
