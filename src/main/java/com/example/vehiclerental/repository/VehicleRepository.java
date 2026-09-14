package com.example.vehiclerental.repository;

import com.example.vehiclerental.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Spring generates the SQL automatically based on the method name!
    Optional<Vehicle> findByVin(String vin);
}