package co.istad.kafka_stream_api.event.order;

public record OrderDetail(
        String orderId,
        String productId,
        Integer qty
) {
}
