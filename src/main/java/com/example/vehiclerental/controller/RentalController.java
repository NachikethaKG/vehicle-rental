package com.example.vehiclerental.controller;

import com.example.vehiclerental.dto.RentalRequest;
import com.example.vehiclerental.entity.RentalAgreement;
import com.example.vehiclerental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalAgreement> rentVehicle(@Valid @RequestBody RentalRequest request) {
        RentalAgreement agreement = rentalService.rentVehicle(request);
        return new ResponseEntity<>(agreement, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<RentalAgreement> returnVehicle(@PathVariable Long id) {
        RentalAgreement agreement = rentalService.returnVehicle(id);
        return ResponseEntity.ok(agreement);
    }
}
