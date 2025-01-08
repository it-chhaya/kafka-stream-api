package co.istad.kafka_stream_api.event.order;

import java.math.BigDecimal;

public record Product(
        String productId,
        String name,
        BigDecimal price,
        String category
) {
}
