package com.homeservice.repository;

import com.homeservice.entity.Booking;
import com.homeservice.entity.User;
import com.homeservice.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByUserOrderByBookingDateDesc(User user);
    long countByWorkerAndStatus(Worker worker, Booking.BookingStatus status);
    long countByWorker(Worker worker);
    List<Booking> findByWorkerOrderByBookingDateDesc(Worker worker);
    List<Booking> findByWorkerAndStatus(Worker worker, Booking.BookingStatus status);
    // Pending bookings for a category (worker can accept)
    List<Booking> findByCategoryAndStatusAndWorkerIsNullOrderByBookingDateDesc(
            com.homeservice.entity.ServiceCategory category, Booking.BookingStatus status);
    long countByStatus(Booking.BookingStatus status);
}
