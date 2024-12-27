package co.istad.kafka_stream_api.service;

import co.istad.kafka_stream_api.event.OrderEvent;
import co.istad.kafka_stream_api.event.OrderReport;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersStoreService ordersStoreService;

    public List<OrderEvent> getOrders() {

        ReadOnlyKeyValueStore<String, OrderEvent> ordersStore =
                ordersStoreService.getOrdersStore();

        List<OrderEvent> orderEvents = new ArrayList<>();

        try (KeyValueIterator<String, OrderEvent> all = ordersStore.all()) {
            while (all.hasNext()) {
                orderEvents.add(all.next().value);
            }
        }

        return orderEvents;
    }

    public OrderReport getOrderReport() {

        ReadOnlyKeyValueStore<String, OrderEvent> ordersStore =
                ordersStoreService.getOrdersStore();

        BigDecimal totalPrice = BigDecimal.ZERO;

        try (KeyValueIterator<String, OrderEvent> all = ordersStore.all()) {
            while (all.hasNext()) {
                totalPrice = totalPrice.add(all.next().value.price());
            }
        }

        return new OrderReport(totalPrice);
    }

}
