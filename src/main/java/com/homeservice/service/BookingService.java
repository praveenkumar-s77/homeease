package com.homeservice.service;

import com.homeservice.entity.*;
import com.homeservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ExpertRepository expertRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public BookingService(BookingRepository bookingRepository, ExpertRepository expertRepository,
                          ServiceCategoryRepository categoryRepository, UserRepository userRepository,
                          WishlistRepository wishlistRepository) {
        this.bookingRepository = bookingRepository;
        this.expertRepository = expertRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public List<ServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ServiceCategory getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public void updateBookingStatus(Long id, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus(status);
            bookingRepository.save(booking);
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Expert management (admin only)
    public List<Expert> getAllExperts() {
        return expertRepository.findAll();
    }

    public List<Expert> getExpertsByCategory(ServiceCategory category) {
        return expertRepository.findByCategory(category);
    }

    public Expert saveExpert(Expert expert) {
        return expertRepository.save(expert);
    }

    public Expert getExpertById(Long id) {
        return expertRepository.findById(id).orElse(null);
    }

    public void deleteExpert(Long id) {
        expertRepository.deleteById(id);
    }

    public ServiceCategory saveCategory(ServiceCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Expert profile stats
    public long getCompletedBookingsCount(Expert expert) {
        return bookingRepository.countByExpertAndStatus(expert, Booking.BookingStatus.COMPLETED);
    }

    public long getTotalBookingsCount(Expert expert) {
        return bookingRepository.countByExpert(expert);
    }

    // Wishlist
    public List<Wishlist> getWishlistByUser(User user) {
        return wishlistRepository.findByUser(user);
    }

    public boolean isExpertWishlisted(User user, Expert expert) {
        return wishlistRepository.existsByUserAndExpert(user, expert);
    }

    public void addToWishlist(User user, Expert expert) {
        if (!wishlistRepository.existsByUserAndExpert(user, expert)) {
            wishlistRepository.save(new Wishlist(user, expert));
        }
    }

    @Transactional
    public void removeFromWishlist(User user, Expert expert) {
        wishlistRepository.deleteByUserAndExpert(user, expert);
    }

    public long getWishlistCount(User user) {
        return wishlistRepository.countByUser(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Expert by user account
    public Expert getExpertByUser(User user) {
        return expertRepository.findByUser(user).orElse(null);
    }

    public List<Booking> getBookingsByExpert(Expert expert) {
        return bookingRepository.findByExpertOrderByBookingDateDesc(expert);
    }

    // Pending requests for a Expert's category (notifications)
    public List<Booking> getPendingRequestsForCategory(ServiceCategory category) {
        return bookingRepository.findByCategoryAndStatusAndExpertIsNullOrderByBookingDateDesc(
                category, Booking.BookingStatus.PENDING);
    }

    public long countByStatus(Booking.BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Expert skills
    public List<Expert> getExpertsBySkill(Long categoryId) {
        return expertRepository.findBySkillCategory(categoryId);
    }

    // Pending requests for all of an Expert's skills
    public List<Booking> getPendingRequestsForSkills(java.util.Set<ServiceCategory> skills) {
        java.util.List<Booking> allPending = new java.util.ArrayList<>();
        for (ServiceCategory skill : skills) {
            allPending.addAll(bookingRepository.findByCategoryAndStatusAndExpertIsNullOrderByBookingDateDesc(
                    skill, Booking.BookingStatus.PENDING));
        }
        return allPending;
    }
}
