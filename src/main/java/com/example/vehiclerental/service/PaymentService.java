package com.example.vehiclerental.service;

import com.example.vehiclerental.entity.Payment;
import com.example.vehiclerental.entity.PaymentStatus;
import com.example.vehiclerental.entity.RentalAgreement;
import com.example.vehiclerental.repository.PaymentRepository;
import com.example.vehiclerental.repository.RentalAgreementRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalAgreementRepository agreementRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws Exception {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    public Map<String, String> createOrder(Long agreementId, BigDecimal amount) throws Exception {
        RentalAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found"));

        JSONObject orderRequest = new JSONObject();
        // Convert currency to smallest unit (INR -> paise, multiply by 100)
        long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValue();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "agmt_" + agreement.getId());

        Order order = razorpayClient.orders.create(orderRequest);
        return Map.of(
                "orderId", order.get("id"),
                "keyId", keyId
        );
    }

    @Transactional
    public Payment verifyAndSavePayment(Long agreementId, BigDecimal amount, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        RentalAgreement agreement = agreementRepository.findById(agreementId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found"));

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(options, keySecret);
            if (!isValid) {
                throw new SecurityException("Cryptographic signature mismatch");
            }
        } catch (Exception e) {
            Payment failed = Payment.builder()
                    .rentalAgreement(agreement)
                    .amount(amount)
                    .status(PaymentStatus.FAILED)
                    .transactionId(razorpayPaymentId != null ? razorpayPaymentId : "failed_" + razorpayOrderId)
                    .createdAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(failed);
            throw new RuntimeException("Signature verification failed: " + e.getMessage());
        }

        Payment payment = Payment.builder()
                .rentalAgreement(agreement)
                .amount(amount)
                .status(PaymentStatus.COMPLETED)
                .transactionId(razorpayPaymentId)
                .createdAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }
}
