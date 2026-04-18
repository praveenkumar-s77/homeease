package com.homeservice.repository;

import com.homeservice.entity.Worker;
import com.homeservice.entity.User;
import com.homeservice.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findByCategory(ServiceCategory category);
    List<Worker> findByCategoryAndAvailableTrue(ServiceCategory category);
    Optional<Worker> findByUser(User user);

    // Find available workers near a location within a radius (in km), sorted by distance
    @Query("SELECT w, " +
           "(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(w.latitude)) * " +
           "COS(RADIANS(w.longitude) - RADIANS(:lng)) + " +
           "SIN(RADIANS(:lat)) * SIN(RADIANS(w.latitude)))) AS distance " +
           "FROM Worker w WHERE w.available = true AND w.category.id = :categoryId " +
           "AND (6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(w.latitude)) * " +
           "COS(RADIANS(w.longitude) - RADIANS(:lng)) + " +
           "SIN(RADIANS(:lat)) * SIN(RADIANS(w.latitude)))) <= :radius " +
           "ORDER BY distance")
    List<Object[]> findNearbyAvailableWorkers(@Param("lat") double lat,
                                              @Param("lng") double lng,
                                              @Param("categoryId") Long categoryId,
                                              @Param("radius") double radius);
}
