package com.example.vehiclerental.repository;

import com.example.vehiclerental.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalAgreementId(Long rentalAgreementId);
}