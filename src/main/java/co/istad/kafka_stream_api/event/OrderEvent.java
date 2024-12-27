package co.istad.kafka_stream_api.event;

import java.math.BigDecimal;

public record OrderEvent(
        String orderId,
        String orderType,
        BigDecimal price
) {
}
