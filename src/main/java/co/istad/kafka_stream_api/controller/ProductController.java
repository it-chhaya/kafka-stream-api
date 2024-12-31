package co.istad.kafka_stream_api.controller;

import co.istad.kafka_stream_api.event.OrderEvent;
import co.istad.kafka_stream_api.event.ProductEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @GetMapping("/count-stock")
    public ResponseEntity<?> countStockReport() {

        ReadOnlyKeyValueStore<String, ProductEvent> keyValueStore = streamsBuilderFactoryBean
                .getKafkaStreams()
                .store(
                        StoreQueryParameters.fromNameAndType(
                                "products-reduce-store",
                                QueryableStoreTypes.keyValueStore()
                        )
                );

        List<ProductEvent> data = new ArrayList<>();

        try (KeyValueIterator<String, ProductEvent> all = keyValueStore.all()) {
            while (all.hasNext()) {
                KeyValue<String, ProductEvent> next = all.next();
                data.add(next.value);
            }
        }

        return ResponseEntity.ok(data);

    }

    @GetMapping("/count")
    public ResponseEntity<?> countReport() {

        ReadOnlyKeyValueStore<String, Long> keyValueStore = streamsBuilderFactoryBean
                .getKafkaStreams()
                .store(
                        StoreQueryParameters.fromNameAndType(
                                "products-count-store",
                                QueryableStoreTypes.keyValueStore()
                        )
                );

        List<Map<String, Long>> data = new ArrayList<>();

        try (KeyValueIterator<String, Long> all = keyValueStore.all()) {
            while (all.hasNext()) {
                KeyValue<String, Long> next = all.next();
                data.add(Map.of(next.key, next.value));
            }
        }

        return ResponseEntity.ok(data);

    }

}
