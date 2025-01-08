package co.istad.kafka_stream_api.event.order;

public record Order(
        String orderId,
        String orderDate,
        String customerId,
        String status // APPROVED, REJECTED, CANCELLED
) {
}
