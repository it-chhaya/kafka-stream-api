package co.istad.kafka_stream_api.event.order;

import lombok.Builder;

@Builder
public record Shipping(
        String shippingId,
        String orderId,
        String address,
        String status,
        String carrier
) {
}
