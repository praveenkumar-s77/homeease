package com.homeservice.service;

import com.homeservice.entity.*;
import com.homeservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final WorkerRepository workerRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public BookingService(BookingRepository bookingRepository, WorkerRepository workerRepository,
                          ServiceCategoryRepository categoryRepository, UserRepository userRepository,
                          WishlistRepository wishlistRepository) {
        this.bookingRepository = bookingRepository;
        this.workerRepository = workerRepository;
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

    // Worker management (admin only)
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public List<Worker> getWorkersByCategory(ServiceCategory category) {
        return workerRepository.findByCategory(category);
    }

    public Worker saveWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public Worker getWorkerById(Long id) {
        return workerRepository.findById(id).orElse(null);
    }

    public void deleteWorker(Long id) {
        workerRepository.deleteById(id);
    }

    public ServiceCategory saveCategory(ServiceCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Worker profile stats
    public long getCompletedBookingsCount(Worker worker) {
        return bookingRepository.countByWorkerAndStatus(worker, Booking.BookingStatus.COMPLETED);
    }

    public long getTotalBookingsCount(Worker worker) {
        return bookingRepository.countByWorker(worker);
    }

    // Wishlist
    public List<Wishlist> getWishlistByUser(User user) {
        return wishlistRepository.findByUser(user);
    }

    public boolean isWorkerWishlisted(User user, Worker worker) {
        return wishlistRepository.existsByUserAndWorker(user, worker);
    }

    public void addToWishlist(User user, Worker worker) {
        if (!wishlistRepository.existsByUserAndWorker(user, worker)) {
            wishlistRepository.save(new Wishlist(user, worker));
        }
    }

    @Transactional
    public void removeFromWishlist(User user, Worker worker) {
        wishlistRepository.deleteByUserAndWorker(user, worker);
    }

    public long getWishlistCount(User user) {
        return wishlistRepository.countByUser(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Worker by user account
    public Worker getWorkerByUser(User user) {
        return workerRepository.findByUser(user).orElse(null);
    }

    public List<Booking> getBookingsByWorker(Worker worker) {
        return bookingRepository.findByWorkerOrderByBookingDateDesc(worker);
    }

    // Pending requests for a worker's category (notifications)
    public List<Booking> getPendingRequestsForCategory(ServiceCategory category) {
        return bookingRepository.findByCategoryAndStatusAndWorkerIsNullOrderByBookingDateDesc(
                category, Booking.BookingStatus.PENDING);
    }

    public long countByStatus(Booking.BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
