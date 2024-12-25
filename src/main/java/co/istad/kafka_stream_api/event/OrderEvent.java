package co.istad.kafka_stream_api.event;

public record OrderEvent(
        String orderId,
        String orderType,
        Double price
) {
}
