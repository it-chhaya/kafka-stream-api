package co.istad.kafka_stream_api.topology;

import co.istad.kafka_stream_api.event.ProductEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class ProductTopology {

    @Value("${topic.products-stock-in}")
    private String productsStockIn;

    @Autowired
    public Topology buildProductTopology(StreamsBuilder streamsBuilder) {

        KTable<String, ProductEvent> productEventKStream = streamsBuilder
                .table(productsStockIn,
                        Consumed.with(Serdes.String(), new JsonSerde<>(ProductEvent.class)),
                        Materialized.as("products-store")
                );


        productEventKStream
                .toStream()
                .print(Printed.<String, ProductEvent>toSysOut().withLabel("Product Event Stream"));

        // Aggregation - Count
        // countAggregate(productEventKStream);

        // Aggregation - Reduce
        reduceAggregate(productEventKStream);

        return streamsBuilder.build();
    }

    private void countAggregate(KTable<String, ProductEvent> productEventKStream) {
        KTable<String, Long> productCount = productEventKStream
                .toStream()
                .groupByKey()
                .count(Materialized.as("products-count-store"));

        productCount
                .toStream()
                .print(Printed.<String, Long>toSysOut().withLabel("Product Count"));
    }

    private void reduceAggregate(KTable<String, ProductEvent> productEventKStream) {
        KTable<String, ProductEvent> reducedProduct = productEventKStream
                .toStream()
                .groupByKey()
                .reduce(
                        (productEvent, newProductEvent) -> {
                            return new ProductEvent(productEvent.code(),
                                    productEvent.name(),
                                    productEvent.category(),
                                    productEvent.quantity() + newProductEvent.quantity(),
                                    productEvent.price());
                        },
                        Materialized.as("products-reduce-store")
                );

        reducedProduct
                .toStream()
                .print(Printed.<String, ProductEvent>toSysOut().withLabel("Product Reduce"));
    }

}
