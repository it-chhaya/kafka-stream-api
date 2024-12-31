package co.istad.kafka_stream_api.event;

import java.math.BigDecimal;

public record ProductEvent(
        String code,
        String name,
        String category,
        Integer quantity,
        BigDecimal price
) {
}
