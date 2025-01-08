package co.istad.kafka_stream_api.test;

import co.istad.kafka_stream_api.event.order.Customer;
import co.istad.kafka_stream_api.event.order.Order;
import co.istad.kafka_stream_api.event.order.OrderDetail;
import co.istad.kafka_stream_api.event.order.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderDataProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TestDataGenerator dataGenerator;

    @Value("${topic.products-topic}")
    private String productsTopic;

    @Value("${topic.customers-topic}")
    private String customersTopic;

    @Value("${topic.orders-topic}")
    private String ordersTopic;

    @Value("${topic.order-details-topic}")
    private String orderDetailsTopic;

    @Value("${topic.payments-topic}")
    private String paymentsTopic;

    @Value("${topic.shipping-topic}")
    private String shippingTopic;

    @Scheduled(fixedRate = 20000) // Generate data every 20 seconds
    public void generateTestData() {
        try {

            // Generate related Customer
            Customer customer = dataGenerator.generateCustomer();

            // Generate related Product
            Product product = dataGenerator.generateProduct();

            // Generate Order
            Order order = dataGenerator.generateOrder(customer.customerId());

            OrderDetail orderDetail = dataGenerator.generateOrderDetail(order.orderId(), product.productId());

            // Send order and related data
            kafkaTemplate.send(customersTopic, customer.customerId(), customer);
            log.info("Sent customer: {}", customer.customerId());

            kafkaTemplate.send(productsTopic, product.productId(), product);
            log.info("Sent product: {}", product.productId());

            kafkaTemplate.send(ordersTopic, order.orderId(), order);
            log.info("Sent order: {}", order.orderId());

            kafkaTemplate.send(orderDetailsTopic, orderDetail.orderId(), orderDetail);
            log.info("Sent order details: {}", orderDetail.orderId());

//            // Send Payment with small delay
//            CompletableFuture.runAsync(() -> {
//                try {
//                    Thread.sleep(1000); // 1 second delay
//                    Payment payment = dataGenerator.generatePayment(order.getOrderId());
//                    kafkaTemplate.send(paymentsTopic, payment.getOrderId(), payment);
//                    log.info("Sent payment for order: {}", payment.getOrderId());
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    log.error("Error in payment delay", e);
//                }
//            });
//
//            // Send Shipping with delay
//            CompletableFuture.runAsync(() -> {
//                try {
//                    Thread.sleep(2000); // 2 seconds delay
//                    Shipping shipping = dataGenerator.generateShipping(order.getOrderId());
//                    kafkaTemplate.send(shippingTopic, shipping.getOrderId(), shipping);
//                    log.info("Sent shipping for order: {}", shipping.getOrderId());
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    log.error("Error in shipping delay", e);
//                }
//            });

        } catch (Exception e) {
            log.error("Error generating test data", e);
        }
    }
}
