package co.istad.kafka_stream_api.event.order;

import lombok.Builder;

@Builder
public record Customer(
        String customerId,
        String name,
        String email
) {
}
