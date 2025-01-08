package co.istad.kafka_stream_api.event.order;

import lombok.Builder;

@Builder
public record OrderDetail(
        String orderId,
        String productId,
        Integer qty
) {
}
