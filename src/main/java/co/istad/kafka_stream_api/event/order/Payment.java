package co.istad.kafka_stream_api.event.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Payment(
        String paymentId,
        String orderId,
        String paymentMethod,
        BigDecimal amount,
        String status
) {
}
