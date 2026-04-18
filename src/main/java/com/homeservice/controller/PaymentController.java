package com.homeservice.controller;

import com.homeservice.entity.Booking;
import com.homeservice.service.BookingService;
import com.homeservice.service.PaymentService;
import com.razorpay.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @PostMapping("/create-order/{bookingId}")
    public ResponseEntity<Map<String, Object>> createOrder(@PathVariable Long bookingId) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Booking not found"));
            }

            Order order = paymentService.createOrder(booking.getAmount());

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", "INR");
            response.put("key", paymentService.getRazorpayKeyId());
            response.put("bookingId", bookingId);

            booking.setPaymentOrderId(order.get("id"));
            bookingService.createBooking(booking);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify/{bookingId}")
    public ResponseEntity<Map<String, String>> verifyPayment(
            @PathVariable Long bookingId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpayOrderId) {

        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Booking not found"));
        }

        booking.setPaymentId(razorpayPaymentId);
        booking.setPaymentStatus(Booking.PaymentStatus.PAID);
        bookingService.createBooking(booking);

        return ResponseEntity.ok(Map.of("status", "success", "message", "Payment verified"));
    }
}
