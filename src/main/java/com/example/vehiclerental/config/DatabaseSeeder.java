package com.example.vehiclerental.config;

import com.example.vehiclerental.entity.Vehicle;
import com.example.vehiclerental.entity.VehicleStatus;
import com.example.vehiclerental.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) {
        // Only seed if the vehicles table is empty
        if (vehicleRepository.count() == 3) {
            List<Vehicle> initialFleet = List.of(
                    createVehicle("Porsche", "911 GT3", "VINPRSH911GT3X", "450.00"),
                    createVehicle("Lamborghini", "Huracán EVO", "VINLMBHURC1234", "850.00"),
                    createVehicle("Ferrari", "F8 Tributo", "VINFERF8TRIB88", "950.00"),
                    createVehicle("Rolls-Royce", "Phantom", "VINRRPHNTM7777", "1200.00"),
                    createVehicle("Audi", "R8 V10 Plus", "VINAUDI10R8PL", "400.00"),
                    createVehicle("Mercedes-Benz", "G63 AMG", "VINMBG63AMG4X4", "350.00"),
                    createVehicle("Land Rover", "Range Rover Sport", "VINLRRSVR2024X", "250.00"),
                    createVehicle("BMW", "M5 Competition", "VINBMW5COMP99X", "280.00"),
                    createVehicle("Tesla", "Model S Plaid", "VINTSLASPLD001", "200.00"),
                    createVehicle("Toyota", "GR Supra", "VINTYTSPRA300Z", "180.00")
            );
            vehicleRepository.saveAll(initialFleet);
            System.out.println("✅ Premium Fleet Seeded Successfully!");
        }
    }

    private Vehicle createVehicle(String make, String model, String vin, String dailyRate) {
        // Assuming your Vehicle entity uses a Builder. Adjust if using standard setters.
        // Also assuming status is stored as a String. If it's an Enum, use VehicleStatus.AVAILABLE
        return Vehicle.builder()
                .make(make)
                .model(model)
                .vin(vin)
                .dailyRate(new BigDecimal(dailyRate))
                .status(VehicleStatus.valueOf("AVAILABLE"))
                .build();
    }
}
