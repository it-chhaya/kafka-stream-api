package co.istad.kafka_stream_api.event.order;

import java.math.BigDecimal;

public record Payment(
        String paymentId,
        String orderId,
        String paymentMethod,
        BigDecimal amount,
        String status
) {
}
