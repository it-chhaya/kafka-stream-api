package co.istad.kafka_stream_api.topology;

import co.istad.kafka_stream_api.event.ProductEvent;
import co.istad.kafka_stream_api.event.ProductStockInReport;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;

@Slf4j
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


        productEventKStream
                .toStream()
                .split()
                .branch((s, productEvent) -> productEvent.category().equals("REGULAR"),
                        Branched.withConsumer(regularProduct -> {
                            // Report Aggregation for Branch 1
                            // REGULAR CATEGORY
                            log.info("Regular product: {}", regularProduct);
                            aggregateProductEventsByBranch(regularProduct, "regular-product-stock-in-report-store");
                        }))
                .branch((s, productEvent) -> productEvent.category().equals("PREMIUM"),
                        Branched.withConsumer(premiumProduct -> {
                            // Report Aggregation for Branch 2
                            // PREMIUM CATEGORY
                            log.info("Premium product: {}", premiumProduct);
                            aggregateProductEventsByBranch(premiumProduct, "premium-product-stock-in-report-store");
                        }));


        // Aggregation - Count
        // countAggregate(productEventKStream);

        // Aggregation - Reduce
        // reduceAggregate(productEventKStream);

        // Aggregation - Aggregator
        // aggregateProductEventsStream(productEventKStream);

        return streamsBuilder.build();
    }


    private void aggregateProductEventsByBranch(KStream<String, ProductEvent> productEventKStream, String storeName) {

        // Step 1 => Define Initializer
        Initializer<ProductStockInReport> initializer =
                () -> new ProductStockInReport("", new ArrayList<>(), 0);

        // Step 2 => Define Aggregator
        Aggregator<String, ProductEvent, ProductStockInReport> aggregator =
                (key, productEvent, productStockInReport) -> {
                    productStockInReport.productEvents().add(productEvent);
                    return new ProductStockInReport(key, productStockInReport.productEvents(), productStockInReport.runningCount() + 1);
                };

        // Step 3 => Start Aggregate
        KTable<String, ProductStockInReport> productStockInReportAggregated =
                productEventKStream
                        .groupByKey()
                        .aggregate(
                                initializer,
                                aggregator,
                                Materialized
                                        .<String, ProductStockInReport, KeyValueStore<Bytes, byte[]>>as(storeName)
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(new JsonSerde<>(ProductStockInReport.class))
                        );

        productStockInReportAggregated
                .toStream()
                .print(Printed.<String, ProductStockInReport>toSysOut().withLabel("productStockInReportAggregated"));
    }


    private void aggregateProductEventsStream(KTable<String, ProductEvent> productEventKStream) {

        // Step 1 => Define Initializer
        Initializer<ProductStockInReport> initializer =
                () -> new ProductStockInReport("", new ArrayList<>(), 0);

        // Step 2 => Define Aggregator
        Aggregator<String, ProductEvent, ProductStockInReport> aggregator =
                (key, productEvent, productStockInReport) -> {
                    productStockInReport.productEvents().add(productEvent);
                    return new ProductStockInReport(key, productStockInReport.productEvents(), productStockInReport.runningCount() + 1);
                };

        // Step 3 => Start Aggregate
        KTable<String, ProductStockInReport> productStockInReportAggregated =
                productEventKStream
                        .toStream()
                        .groupByKey()
                        .aggregate(
                                initializer,
                                aggregator,
                                Materialized
                                        .<String, ProductStockInReport, KeyValueStore<Bytes, byte[]>>as("product-stock-in-report-store")
                                        .withKeySerde(Serdes.String())
                                        .withValueSerde(new JsonSerde<>(ProductStockInReport.class))
                        );

        productStockInReportAggregated
                .toStream()
                .print(Printed.<String, ProductStockInReport>toSysOut().withLabel("productStockInReportAggregated"));
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
                        (productEvent, newProductEvent) -> new ProductEvent(productEvent.code(),
                                productEvent.name(),
                                productEvent.category(),
                                productEvent.quantity() + newProductEvent.quantity(),
                                productEvent.price()),
                        Materialized.as("products-reduce-store")
                );

        reducedProduct
                .toStream()
                .print(Printed.<String, ProductEvent>toSysOut().withLabel("Product Reduce"));
    }

}
