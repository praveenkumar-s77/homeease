package com.homeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "workers")
public class Expert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String phone;
    private String address;
    private double hourlyRate;
    private boolean available = true;
    private double rating = 0.0;
    private int totalBookings = 0;
    private double latitude;
    private double longitude;

    // ID Verification
    @Enumerated(EnumType.STRING)
    private IdType idType;

    private String idNumber; // Aadhaar / PAN / DL number

    private boolean verified = false;

    public enum IdType { AADHAAR, PAN, DRIVING_LICENCE }

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "worker_skills",
            joinColumns = @JoinColumn(name = "worker_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private java.util.Set<ServiceCategory> skills = new java.util.HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Expert() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
    public IdType getIdType() { return idType; }
    public void setIdType(IdType idType) { this.idType = idType; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public java.util.Set<ServiceCategory> getSkills() { return skills; }
    public void setSkills(java.util.Set<ServiceCategory> skills) { this.skills = skills; }
}
