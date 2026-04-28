package com.homeservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wishlists", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "worker_id"}))
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Expert expert;

    private LocalDateTime addedAt = LocalDateTime.now();

    public Wishlist() {}

    public Wishlist(User user, Expert expert) {
        this.user = user;
        this.expert = expert;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Expert getExpert() { return expert; }
    public void setExpert(Expert expert) { this.expert = expert; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
