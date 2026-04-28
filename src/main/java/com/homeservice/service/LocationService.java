package com.homeservice.service;

import com.homeservice.entity.Expert;
import com.homeservice.repository.ExpertRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final ExpertRepository expertRepository;

    @Value("${app.default.search.radius}")
    private double defaultRadius;

    public LocationService(ExpertRepository expertRepository) {
        this.expertRepository = expertRepository;
    }

    public List<NearbyExpert> findNearbyExperts(double lat, double lng, Long categoryId, Double radius) {
        double searchRadius = (radius != null) ? radius : defaultRadius;
        List<Object[]> results = expertRepository.findNearbyAvailableExperts(lat, lng, categoryId, searchRadius);

        List<NearbyExpert> nearbyExperts = new ArrayList<>();
        for (Object[] row : results) {
            Expert expert = (Expert) row[0];
            double distance = (Double) row[1];
            nearbyExperts.add(new NearbyExpert(expert, Math.round(distance * 10.0) / 10.0));
        }
        return nearbyExperts;
    }

    public static class NearbyExpert {
        private final Expert expert;
        private final double distanceKm;

        public NearbyExpert(Expert expert, double distanceKm) {
            this.expert = expert;
            this.distanceKm = distanceKm;
        }

        public Expert getExpert() { return expert; }
        public double getDistanceKm() { return distanceKm; }
    }
}
