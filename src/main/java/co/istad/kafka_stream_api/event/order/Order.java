package co.istad.kafka_stream_api.event.order;

import lombok.Builder;

@Builder
public record Order(
        String orderId,
        String orderDate,
        String customerId,
        String status // APPROVED, REJECTED, CANCELLED
) {
}
