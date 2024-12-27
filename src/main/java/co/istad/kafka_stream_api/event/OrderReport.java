package co.istad.kafka_stream_api.event;

import java.math.BigDecimal;

public record OrderReport(
        BigDecimal totalPrice
) {
}
