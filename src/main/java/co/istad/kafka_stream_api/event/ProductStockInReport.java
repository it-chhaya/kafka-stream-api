package co.istad.kafka_stream_api.event;

import java.util.List;

public record ProductStockInReport(
        String key,
        List<ProductEvent> productEvents,
        Integer runningCount
) {
}
