package co.istad.kafka_stream_api.event.order;

public record Shipping(
        String shippingId,
        String orderId,
        String address,
        String status,
        String carrier
) {
}
