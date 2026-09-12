package com.example.vehiclerental.service;

import com.example.vehiclerental.dto.RentalRequest;
import com.example.vehiclerental.entity.RentalAgreement;
import com.example.vehiclerental.entity.RentalStatus;
import com.example.vehiclerental.entity.Vehicle;
import com.example.vehiclerental.entity.VehicleStatus;
import com.example.vehiclerental.exception.ResourceNotFoundException;
import com.example.vehiclerental.exception.VehicleNotAvailableException;
import com.example.vehiclerental.repository.RentalAgreementRepository;
import com.example.vehiclerental.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final VehicleRepository vehicleRepository;
    private final RentalAgreementRepository agreementRepository;

    @Transactional
    public RentalAgreement rentVehicle(RentalRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + request.getVehicleId() + " not found."));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new VehicleNotAvailableException("Vehicle is currently " + vehicle.getStatus());
        }

        // 1. Update vehicle state
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);

        // 2. Create the agreement
        RentalAgreement agreement = RentalAgreement.builder()
                .vehicle(vehicle)
                .customerName(request.getCustomerName())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(request.getRentalDays()))
                .status(RentalStatus.ACTIVE)
                .build();

        return agreementRepository.save(agreement);
    }

    @Transactional
    public RentalAgreement returnVehicle(Long agreementId) {
        RentalAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement with ID " + agreementId + " not found."));

        if (agreement.getStatus() == RentalStatus.COMPLETED) {
            throw new IllegalStateException("This agreement is already completed.");
        }

        Vehicle vehicle = agreement.getVehicle();

        // 1. Revert vehicle state
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        // 2. Calculate actual days rented (minimum of 1 day to avoid multiplying by 0)
        long daysRented = ChronoUnit.DAYS.between(agreement.getStartDate(), LocalDate.now());
        if (daysRented == 0) {
            daysRented = 1;
        }

        // 3. Calculate cost and finalize agreement
        BigDecimal totalCost = vehicle.getDailyRate().multiply(BigDecimal.valueOf(daysRented));

        agreement.setEndDate(LocalDate.now());
        agreement.setTotalCost(totalCost);
        agreement.setStatus(RentalStatus.COMPLETED);

        return agreementRepository.save(agreement);
    }
}
