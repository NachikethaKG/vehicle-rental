package com.example.vehiclerental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    @Min(value = 1, message = "Rental duration must be at least 1 day")
    private int rentalDays;
}
