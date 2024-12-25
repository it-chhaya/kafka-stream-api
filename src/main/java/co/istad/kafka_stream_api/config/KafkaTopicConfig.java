package co.istad.kafka_stream_api.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${topic.orders-in}")
    private String ordersIn;

    @Value("${topic.orders-out}")
    private String ordersOut;

    @Bean
    public NewTopic ordersEventTopicIn() {
        return TopicBuilder
                .name(ordersIn)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic ordersEventTopicOut() {
        return TopicBuilder
                .name(ordersOut)
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic wordsEventTopic() {
        return TopicBuilder
                .name("words")
                .partitions(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic wordsEventTopicOut() {
        return TopicBuilder
                .name("out-words")
                .partitions(1)
                .compact()
                .build();
    }

}
