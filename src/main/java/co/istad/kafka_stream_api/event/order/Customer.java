package co.istad.kafka_stream_api.event.order;

public record Customer(
        String customerId,
        String name,
        String email
) {
}
