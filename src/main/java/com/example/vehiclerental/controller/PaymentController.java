package com.example.vehiclerental.controller;

import com.example.vehiclerental.entity.Payment;
import com.example.vehiclerental.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> payload) {
        try {
            Long agreementId = Long.valueOf(payload.get("rentalAgreementId").toString());
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            return ResponseEntity.ok(paymentService.createOrder(agreementId, amount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            Long agreementId = Long.valueOf(payload.get("rentalAgreementId"));
            BigDecimal amount = new BigDecimal(payload.get("amount"));
            String orderId = payload.get("razorpay_order_id");
            String paymentId = payload.get("razorpay_payment_id");
            String signature = payload.get("razorpay_signature");

            Payment payment = paymentService.verifyAndSavePayment(agreementId, amount, orderId, paymentId, signature);
            return ResponseEntity.ok(Map.of(
                    "status", payment.getStatus().name(),
                    "transactionId", payment.getTransactionId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
