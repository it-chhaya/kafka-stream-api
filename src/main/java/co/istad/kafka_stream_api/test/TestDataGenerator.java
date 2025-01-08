package co.istad.kafka_stream_api.test;

import co.istad.kafka_stream_api.event.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TestDataGenerator {
    private final Random random = new Random();

    private final List<String> productCategories = Arrays.asList(
            "Electronics", "Books", "Clothing", "Home & Kitchen", "Sports"
    );

    private final List<String> paymentMethods = Arrays.asList(
            "Credit Card", "PayPal", "Bank Transfer", "Digital Wallet"
    );

    private final List<String> carriers = Arrays.asList(
            "Vireak Bunthan", "GRAB", "VIP", "Food Panda"
    );

    public Order generateOrder(String customerId) {
        return Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now().toString())
                .customerId(customerId)
                .orderDate(LocalDateTime.now().toString())
                .status("PENDING")
                .build();
    }

    public OrderDetail generateOrderDetail(String orderId, String productId) {
        return OrderDetail.builder()
                .orderId(orderId)
                .productId(productId)
                .qty(random.nextInt(100))
                .build();
    }

    public Customer generateCustomer() {
        return Customer.builder()
                .customerId(UUID.randomUUID().toString())
                .name("Customer " + random.nextInt(1000))
                .email("customer" + random.nextInt(1000) + "@example.com")
                .build();
    }

    public Product generateProduct() {
        return Product.builder()
                .productId(UUID.randomUUID().toString())
                .name("Product " + random.nextInt(1000))
                .price(BigDecimal.valueOf(random.nextDouble() * 500))
                .category(productCategories.get(random.nextInt(productCategories.size())))
                .build();
    }

    public Payment generatePayment(String orderId) {
        return Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(orderId)
                .paymentMethod(paymentMethods.get(random.nextInt(paymentMethods.size())))
                .amount(BigDecimal.valueOf(random.nextDouble() * 1000))
                .status("COMPLETED")
                .build();
    }

    public Shipping generateShipping(String orderId) {
        return Shipping.builder()
                .shippingId(UUID.randomUUID().toString())
                .orderId(orderId)
                .address(random.nextInt(999) + " Test St, Test City, TS " + random.nextInt(99999))
                .status("PROCESSING")
                .carrier(carriers.get(random.nextInt(carriers.size())))
                .build();
    }

}
