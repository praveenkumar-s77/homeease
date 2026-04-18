package com.homeservice.repository;

import com.homeservice.entity.User;
import com.homeservice.entity.Worker;
import com.homeservice.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser(User user);
    Optional<Wishlist> findByUserAndWorker(User user, Worker worker);
    boolean existsByUserAndWorker(User user, Worker worker);
    long countByUser(User user);
    void deleteByUserAndWorker(User user, Worker worker);
}
