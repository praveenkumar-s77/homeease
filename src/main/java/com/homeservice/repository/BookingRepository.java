package com.homeservice.repository;

import com.homeservice.entity.Booking;
import com.homeservice.entity.User;
import com.homeservice.entity.Expert;
import com.homeservice.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
    List<Booking> findByUserOrderByBookingDateDesc(User user);
    long countByExpertAndStatus(Expert expert, Booking.BookingStatus status);
    long countByExpert(Expert expert);
    List<Booking> findByExpertOrderByBookingDateDesc(Expert expert);
    List<Booking> findByExpertAndStatus(Expert expert, Booking.BookingStatus status);
    List<Booking> findByCategoryAndStatusAndExpertIsNullOrderByBookingDateDesc(
            ServiceCategory category, Booking.BookingStatus status);
    long countByStatus(Booking.BookingStatus status);
}
