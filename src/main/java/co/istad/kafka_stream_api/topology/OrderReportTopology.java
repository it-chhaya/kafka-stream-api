package co.istad.kafka_stream_api.topology;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.springframework.stereotype.Component;

@Component
public class OrderReportTopology {

    public Topology buildOrderReportTopology(StreamsBuilder streamsBuilder) {

        // 1. Stream data from products-topic (KTable)

        // 2. Stream data from customers-topic (KTable)

        // 3. Stream data from orders-topic (KStream)

        // Join (KStream-KTable)

        return streamsBuilder.build();
    }

}
