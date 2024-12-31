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

    @Value("${topic.products-stock-in}")
    private String productsStockIn;

    @Bean
    public NewTopic productsStockInTopic() {
        return TopicBuilder
                .name(productsStockIn)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic ordersEventTopicIn() {
        return TopicBuilder
                .name(ordersIn)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic ordersEventTopicOut() {
        return TopicBuilder
                .name(ordersOut)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic wordsEventTopic() {
        return TopicBuilder
                .name("words")
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic wordsEventTopicOut() {
        return TopicBuilder
                .name("out-words")
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

}
