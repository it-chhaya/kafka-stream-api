package co.istad.kafka_stream_api.topology;

import co.istad.kafka_stream_api.event.OrderEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class OrdersTopology {

    @Value("${topic.orders-in}")
    private String ordersIn;

    @Value("${topic.orders-out}")
    private String ordersOut;

    @Value("${topic.orders-store}")
    private String ordersStore;

    @Autowired
    public Topology ordersTopology(StreamsBuilder streamsBuilder) {

        // Step 1: Stream data from topic
        KTable<String, OrderEvent> orders = streamsBuilder
                .table(
                        ordersIn,
                        Consumed.with(Serdes.String(), new JsonSerde<>(OrderEvent.class)),
                        Materialized.as(ordersStore)
                );

        orders
                .toStream()
                .print(Printed.<String, OrderEvent>toSysOut().withLabel("orders"));

        // Step 2: Logic
        KTable<String, OrderEvent> ordersProcessed = orders
                .filter((key, value) -> value.price() >= 100);

        ordersProcessed
                .toStream()
                .print(Printed.<String, OrderEvent>toSysOut().withLabel("orders-processed"));

        ordersProcessed
                .toStream()
                .to(
                        ordersOut,
                        Produced.with(Serdes.String(), new JsonSerde<>(OrderEvent.class))
                );

        return streamsBuilder.build();
    }

}
