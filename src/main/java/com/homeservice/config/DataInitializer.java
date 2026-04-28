package com.homeservice.config;

import com.homeservice.entity.*;
import com.homeservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo, ServiceCategoryRepository categoryRepo,
                               ExpertRepository expertRepo, PasswordEncoder encoder) {
        return args -> {
            // Create admin user
            if (!userRepo.existsByUsername("admin")) {
                User admin = new User("admin", encoder.encode("Admin@7749"),
                        "Administrator", "admin@homeservice.com", "9999999999", User.Role.ADMIN);
                userRepo.save(admin);
            }

            // Create demo user
            if (!userRepo.existsByUsername("user")) {
                User user = new User("user", encoder.encode("user123"),
                        "Demo User", "user@homeservice.com", "8888888888", User.Role.USER);
                user.setPermanentAddress("123 Main Street, Bangalore");
                userRepo.save(user);
            }

            // Create demo Expert user account
            User expertUser = null;
            if (!userRepo.existsByUsername("expert")) {
                expertUser = new User("expert", encoder.encode("Expert@7749"),
                        "Manjunath Gowda", "manjunath@homeservice.com", "9876543201", User.Role.EXPERT);
                expertUser.setPermanentAddress("Kengeri, Bangalore 560059");
                userRepo.save(expertUser);
            } else {
                expertUser = userRepo.findByUsername("expert").orElse(null);
            }

            // Create service categories (only if empty)
            if (categoryRepo.count() == 0) {
            ServiceCategory plumbing = new ServiceCategory("Plumbing", "Pipe repairs, leaks, installations");
            ServiceCategory electrical = new ServiceCategory("Electrical", "Wiring, switches, appliance repair");
            ServiceCategory tiles = new ServiceCategory("Tiles & Flooring", "Tile installation, floor repair");
            ServiceCategory cleaning = new ServiceCategory("Syntex & Tank Cleaning", "Water tank and syntex cleaning");
            ServiceCategory painting = new ServiceCategory("Painting", "Interior and exterior painting");
            ServiceCategory carpentry = new ServiceCategory("Carpentry", "Furniture repair, woodwork");

            categoryRepo.save(plumbing);
            categoryRepo.save(electrical);
            categoryRepo.save(tiles);
            categoryRepo.save(cleaning);
            categoryRepo.save(painting);
            categoryRepo.save(carpentry);

            // Create sample workers (with location coordinates - example: Chandigarh area)
            Expert w1 = new Expert(); w1.setName("Ramesh Kumar"); w1.setPhone("7777777771");
            w1.setAddress("Sector 15"); w1.setHourlyRate(300); w1.setCategory(plumbing); w1.setRating(4.5);
            w1.setLatitude(30.7410); w1.setLongitude(76.7684);
            w1.setIdType(Expert.IdType.AADHAAR); w1.setIdNumber("XXXX-XXXX-1234"); w1.setVerified(true);
            expertRepo.save(w1);

            Expert w2 = new Expert(); w2.setName("Suresh Sharma"); w2.setPhone("7777777772");
            w2.setAddress("Sector 22"); w2.setHourlyRate(350); w2.setCategory(electrical); w2.setRating(4.2);
            w2.setLatitude(30.7333); w2.setLongitude(76.7794);
            w2.setIdType(Expert.IdType.PAN); w2.setIdNumber("ABCDE1234F"); w2.setVerified(true);
            expertRepo.save(w2);

            Expert w3 = new Expert(); w3.setName("Mohan Singh"); w3.setPhone("7777777773");
            w3.setAddress("Sector 8"); w3.setHourlyRate(400); w3.setCategory(tiles); w3.setRating(4.8);
            w3.setLatitude(30.7500); w3.setLongitude(76.7800);
            w3.setIdType(Expert.IdType.DRIVING_LICENCE); w3.setIdNumber("DL-1234567890"); w3.setVerified(true);
            expertRepo.save(w3);

            Expert w4 = new Expert(); w4.setName("Vikram Yadav"); w4.setPhone("7777777774");
            w4.setAddress("Sector 11"); w4.setHourlyRate(250); w4.setCategory(cleaning); w4.setRating(4.0);
            w4.setLatitude(30.7450); w4.setLongitude(76.7750);
            w4.setIdType(Expert.IdType.AADHAAR); w4.setIdNumber("XXXX-XXXX-5678"); w4.setVerified(true);
            expertRepo.save(w4);

            Expert w5 = new Expert(); w5.setName("Ajay Verma"); w5.setPhone("7777777775");
            w5.setAddress("Sector 35"); w5.setHourlyRate(280); w5.setCategory(plumbing); w5.setRating(4.3);
            w5.setLatitude(30.7200); w5.setLongitude(76.7600);
            w5.setIdType(Expert.IdType.PAN); w5.setIdNumber("FGHIJ5678K"); w5.setVerified(false);
            expertRepo.save(w5);

            Expert w6 = new Expert(); w6.setName("Deepak Negi"); w6.setPhone("7777777776");
            w6.setAddress("Sector 44"); w6.setHourlyRate(320); w6.setCategory(electrical); w6.setRating(4.6);
            w6.setLatitude(30.7100); w6.setLongitude(76.7500);
            w6.setIdType(Expert.IdType.AADHAAR); w6.setIdNumber("XXXX-XXXX-9012"); w6.setVerified(true);
            expertRepo.save(w6);

            // --- Kengeri, Bangalore 560059 workers ---

            Expert k1 = new Expert(); k1.setName("Manjunath Gowda"); k1.setPhone("9876543201");
            k1.setAddress("Kengeri, Bangalore 560059"); k1.setHourlyRate(350); k1.setCategory(plumbing);
            k1.setRating(4.7); k1.setTotalBookings(85);
            k1.setLatitude(12.9063); k1.setLongitude(77.4853);
            k1.setIdType(Expert.IdType.AADHAAR); k1.setIdNumber("XXXX-XXXX-3456"); k1.setVerified(true);
            k1.setUser(expertUser);
            k1.setSkills(new java.util.HashSet<>(java.util.Arrays.asList(plumbing, electrical)));
            expertRepo.save(k1);

            Expert k2 = new Expert(); k2.setName("Ravi Naik"); k2.setPhone("9876543202");
            k2.setAddress("Kengeri, Bangalore 560059"); k2.setHourlyRate(400); k2.setCategory(electrical);
            k2.setRating(4.3); k2.setTotalBookings(62);
            k2.setLatitude(12.9080); k2.setLongitude(77.4870);
            k2.setIdType(Expert.IdType.PAN); k2.setIdNumber("KLMNO6789P"); k2.setVerified(true);
            expertRepo.save(k2);

            Expert k3 = new Expert(); k3.setName("Srinivas Reddy"); k3.setPhone("9876543203");
            k3.setAddress("Kengeri, Bangalore 560059"); k3.setHourlyRate(450); k3.setCategory(tiles);
            k3.setRating(4.9); k3.setTotalBookings(120);
            k3.setLatitude(12.9045); k3.setLongitude(77.4840);
            k3.setIdType(Expert.IdType.DRIVING_LICENCE); k3.setIdNumber("KA-DL-2024-78901"); k3.setVerified(true);
            expertRepo.save(k3);

            Expert k4 = new Expert(); k4.setName("Venkatesh B"); k4.setPhone("9876543204");
            k4.setAddress("Kengeri, Bangalore 560059"); k4.setHourlyRate(300); k4.setCategory(painting);
            k4.setRating(3.8); k4.setTotalBookings(34);
            k4.setLatitude(12.9070); k4.setLongitude(77.4830);
            k4.setIdType(Expert.IdType.AADHAAR); k4.setIdNumber("XXXX-XXXX-7890"); k4.setVerified(true);
            expertRepo.save(k4);

            Expert k5 = new Expert(); k5.setName("Nagesh M"); k5.setPhone("9876543205");
            k5.setAddress("Kengeri, Bangalore 560059"); k5.setHourlyRate(280); k5.setCategory(carpentry);
            k5.setRating(4.1); k5.setTotalBookings(48);
            k5.setLatitude(12.9055); k5.setLongitude(77.4865);
            k5.setIdType(Expert.IdType.PAN); k5.setIdNumber("QRSTU1234V"); k5.setVerified(true);
            expertRepo.save(k5);
            } // end if categoryRepo.count() == 0
        };
    }
}
