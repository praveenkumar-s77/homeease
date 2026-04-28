package com.homeservice.repository;

import com.homeservice.entity.Expert;
import com.homeservice.entity.User;
import com.homeservice.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert, Long> {
    List<Expert> findByCategory(ServiceCategory category);
    List<Expert> findByCategoryAndAvailableTrue(ServiceCategory category);
    Optional<Expert> findByUser(User user);

    // Find workers who have a specific skill
    @Query("SELECT w FROM Expert w JOIN w.skills s WHERE s.id = :categoryId AND w.available = true")
    List<Expert> findBySkillCategory(@Param("categoryId") Long categoryId);

    // Find available workers near a location within a radius (in km), sorted by distance
    @Query("SELECT w, " +
           "(6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(w.latitude)) * " +
           "COS(RADIANS(w.longitude) - RADIANS(:lng)) + " +
           "SIN(RADIANS(:lat)) * SIN(RADIANS(w.latitude)))) AS distance " +
           "FROM Expert w WHERE w.available = true AND w.category.id = :categoryId " +
           "AND (6371 * ACOS(COS(RADIANS(:lat)) * COS(RADIANS(w.latitude)) * " +
           "COS(RADIANS(w.longitude) - RADIANS(:lng)) + " +
           "SIN(RADIANS(:lat)) * SIN(RADIANS(w.latitude)))) <= :radius " +
           "ORDER BY distance")
    List<Object[]> findNearbyAvailableExperts(@Param("lat") double lat,
                                              @Param("lng") double lng,
                                              @Param("categoryId") Long categoryId,
                                              @Param("radius") double radius);
}
