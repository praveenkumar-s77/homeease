package com.homeservice.service;

import com.homeservice.entity.Worker;
import com.homeservice.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    private final WorkerRepository workerRepository;

    @Value("${app.default.search.radius}")
    private double defaultRadius;

    public LocationService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    public List<NearbyWorker> findNearbyWorkers(double lat, double lng, Long categoryId, Double radius) {
        double searchRadius = (radius != null) ? radius : defaultRadius;
        List<Object[]> results = workerRepository.findNearbyAvailableWorkers(lat, lng, categoryId, searchRadius);

        List<NearbyWorker> nearbyWorkers = new ArrayList<>();
        for (Object[] row : results) {
            Worker worker = (Worker) row[0];
            double distance = (Double) row[1];
            nearbyWorkers.add(new NearbyWorker(worker, Math.round(distance * 10.0) / 10.0));
        }
        return nearbyWorkers;
    }

    public static class NearbyWorker {
        private final Worker worker;
        private final double distanceKm;

        public NearbyWorker(Worker worker, double distanceKm) {
            this.worker = worker;
            this.distanceKm = distanceKm;
        }

        public Worker getWorker() { return worker; }
        public double getDistanceKm() { return distanceKm; }
    }
}
