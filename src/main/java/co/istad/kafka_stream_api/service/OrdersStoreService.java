package co.istad.kafka_stream_api.service;

import co.istad.kafka_stream_api.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdersStoreService {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Value("${topic.orders-store}")
    private String ordersStore;

    public ReadOnlyKeyValueStore<String, OrderEvent> getOrdersStore() {
        return streamsBuilderFactoryBean
                .getKafkaStreams()
                .store(
                        StoreQueryParameters.fromNameAndType(
                                ordersStore,
                                QueryableStoreTypes.keyValueStore()
                        )
                );
    }

}
