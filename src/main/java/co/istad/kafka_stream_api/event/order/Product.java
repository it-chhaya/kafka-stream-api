package co.istad.kafka_stream_api.event.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Product(
        String productId,
        String name,
        BigDecimal price,
        String category
) {
}
