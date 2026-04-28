package com.homeservice.repository;

import com.homeservice.entity.User;
import com.homeservice.entity.Expert;
import com.homeservice.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser(User user);
    Optional<Wishlist> findByUserAndExpert(User user, Expert expert);
    boolean existsByUserAndExpert(User user, Expert expert);
    long countByUser(User user);
    void deleteByUserAndExpert(User user, Expert expert);
}
