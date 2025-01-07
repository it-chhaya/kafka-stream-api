package co.istad.kafka_stream_api.topology;

import co.istad.kafka_stream_api.event.AlphabetReport;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JoinTopology {

    @Bean
    public Topology buildTopology(StreamsBuilder streamsBuilder) {

        // Stream from topic
        KStream<String, String> alphabet = streamsBuilder
                .stream("alphabet",
                        Consumed.with(Serdes.String(), Serdes.String()));

        alphabet
                .print(Printed.<String, String>toSysOut().withLabel("alphabet"));

        KStream<String, String> alphabetMeaning = streamsBuilder
                .stream("alphabet-meaning",
                        Consumed.with(Serdes.String(), Serdes.String())
                );

        // Define ValueJoiner
        // TODO
        ValueJoiner<String, String, AlphabetReport> joiner = AlphabetReport::new;

        // KeyValueMapper<String, String, String> keyValueMapper = (key, value) -> key;

        StreamJoined<String, String, String> joined = StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String());

        JoinWindows joinWindows = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(20));

        KStream<String, AlphabetReport> finalAlphabetReport = alphabet
                .join(alphabetMeaning, joiner, joinWindows, joined);

        finalAlphabetReport
                .print(Printed.<String, AlphabetReport>toSysOut().withLabel("joined-alphabet-report"));

        return streamsBuilder.build();
    }

}
