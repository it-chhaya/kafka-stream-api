package co.istad.kafka_stream_api.topology;

import co.istad.kafka_stream_api.event.ProductStockInReport;
import co.istad.kafka_stream_api.event.order.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;

@Component
public class OrderReportTopology {

    @Value("${topic.products-topic}")
    private String productsTopic;

    @Value("${topic.customers-topic}")
    private String customersTopic;

    @Value("${topic.orders-topic}")
    private String ordersTopic;

    @Value("${topic.order-details-topic}")
    private String orderDetailsTopic;

    @Value("${topic.payments-topic}")
    private String paymentsTopic;

    @Value("${topic.shipping-topic}")
    private String shippingTopic;

    @Bean
    public Topology buildOrderReportTopology(StreamsBuilder streamsBuilder) {

        // 1. Stream data from products-topic (KTable)
        KTable<String, Product> products = streamsBuilder
                .table(productsTopic,
                        Consumed.with(
                                Serdes.String(),
                                new JsonSerde<>(Product.class)
                        ),
                        Materialized.as("report-products-store")
                );

        products
                .toStream()
                .print(Printed.<String, Product>toSysOut().withLabel(productsTopic));

        // 2. Stream data from customers-topic (KTable)
        KTable<String, Customer> customers = streamsBuilder
                .table(customersTopic,
                        Consumed.with(
                                Serdes.String(),
                                new JsonSerde<>(Customer.class)
                        ),
                        Materialized.as("report-customers-store")
                );

        customers
                .toStream()
                .print(Printed.<String, Customer>toSysOut().withLabel(customersTopic));

        // 3. Stream data from orders-topic (KStream)
        KStream<String, Order> orders = streamsBuilder
                .stream(ordersTopic,
                        Consumed.with(
                                Serdes.String(),
                                new JsonSerde<>(Order.class)
                        ))
                .selectKey((key, value) -> value.customerId());

        // 4. Stream data from order-details-topic (KStream)
        KStream<String, OrderDetail> orderDetails = streamsBuilder
                .stream(orderDetailsTopic,
                        Consumed.with(
                                Serdes.String(),
                                new JsonSerde<>(OrderDetail.class)
                        ));

        orders
                .print(Printed.<String, Order>toSysOut().withLabel(ordersTopic));


        // Stream payment
        KStream<String, Payment> payments = streamsBuilder
                .stream(
                        paymentsTopic,
                        Consumed.with(Serdes.String(), new JsonSerde<>(Payment.class))
                );

        // Stream shipping
        KStream<String, Shipping> shipping = streamsBuilder
                .stream(
                        shippingTopic,
                        Consumed.with(Serdes.String(), new JsonSerde<>(Shipping.class))
                );


        // Join (KStream-KTable = Order with Customer)

        ValueJoiner<Order, Customer, OrderReport> joiner =
                (order, customer) -> {
                    OrderReport report = new OrderReport();
                    report.setOrderId(order.orderId());
                    report.setOrderDate(order.orderDate());
                    report.setStatus(order.status());
                    report.setCustomer(customer);
                    return report;
                };

        KStream<String, OrderReport> orderReports = orders
                .join(
                        customers,
                        joiner
                )
                .selectKey((key, value) -> value.getOrderId());

        orderReports
                .print(Printed.<String, OrderReport>toSysOut().withLabel("order-report-joined-1"));

        // Join (KStream - KStream, OrderReport with OrderDetail)

        ValueJoiner<OrderReport, OrderDetail, OrderReport> orderReportJoinOrderDetails =
                (orderReport, orderDetail) -> {
                    orderReport.setOrderDetails(new ArrayList<>());
                    orderReport.getOrderDetails().add(orderDetail);
                    return orderReport;
                };

        StreamJoined<String, OrderReport, OrderDetail> orderReportStreamJoined =
                StreamJoined.with(Serdes.String(), new JsonSerde<>(OrderReport.class), new JsonSerde<>(OrderDetail.class));

        JoinWindows joinWindows = JoinWindows
                .ofTimeDifferenceWithNoGrace(Duration.ofSeconds(20));

        TimeWindows tumblingWindows = TimeWindows
                .ofSizeWithNoGrace(Duration.ofDays(30));

        KStream<String, OrderReport> orderReportsV2 = orderReports
                .join(orderDetails,
                        orderReportJoinOrderDetails,
                        joinWindows,
                        orderReportStreamJoined);

        orderReportsV2
                //.groupByKey()
                //.windowedBy(tumblingWindows)
                .print(Printed.<String, OrderReport>toSysOut().withLabel("order-report-joined-2"));


        // Join order report v2 - payment

        ValueJoiner<OrderReport, Payment, OrderReport> orderReportJoinPayment =
                (orderReport, payment) -> {
                    orderReport.setPayment(payment);
                    return orderReport;
                };

        StreamJoined<String, OrderReport, Payment> orderReportStreamJoinPayment =
                StreamJoined.with(Serdes.String(), new JsonSerde<>(OrderReport.class), new JsonSerde<>(Payment.class));

        KStream<String, OrderReport> orderReportsV3 = orderReportsV2
                .join(
                        payments,
                        orderReportJoinPayment,
                        joinWindows,
                        orderReportStreamJoinPayment
                );

        // Convert KStream to KTable (Create State Store)
        orderReportsV3
                .toTable(
                        Named.as("order-report-v3"),
                        Materialized
                                .<String, OrderReport, KeyValueStore<Bytes, byte[]>>
                                        as("order-report-v3")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(OrderReport.class))
                );



        return streamsBuilder.build();
    }

}
