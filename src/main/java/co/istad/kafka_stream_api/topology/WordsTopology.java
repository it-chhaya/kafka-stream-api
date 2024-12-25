package co.istad.kafka_stream_api.topology;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class WordsTopology {

    @Autowired
    Topology build(StreamsBuilder streamsBuilder) {

        // 1.Stream form Source
        KStream<String, String> words = streamsBuilder
                .stream("words",
                        Consumed.with(Serdes.String(), Serdes.String()));

//        words
//                .print(Printed.<String, String>toSysOut()
//                        .withLabel("original-words")
//                );

        // 2.Process stream
        KStream<String, String> wordsProceed = words
                .filter((key, value) -> value.length() > 3)
                .flatMap((key, value) -> {
                    var newValue = Arrays.asList(value.split(""));
                    var keyValueList = newValue
                            .stream().map(t -> KeyValue.pair(key.toUpperCase(), t))
                            .collect(Collectors.toList());
                    return keyValueList;
                });
                //.mapValues(value -> value.toUpperCase());

        wordsProceed
                .print(Printed.<String, String>toSysOut()
                        .withLabel("proceed-words")
                );

        // 3.Output stream
        wordsProceed
                .to(
                        "out-words",
                        Produced.with(Serdes.String(), Serdes.String())
                );

        return streamsBuilder.build();
    }

}
