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
    public NewTopic productsTopic() {
        return TopicBuilder
                .name(productsTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic customersTopic() {
        return TopicBuilder
                .name(customersTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder
                .name(ordersTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic orderDetailsTopic() {
        return TopicBuilder
                .name(orderDetailsTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder
                .name(paymentsTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic shippingTopic() {
        return TopicBuilder
                .name(shippingTopic)
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic alphabetTopic() {
        return TopicBuilder
                .name("alphabet")
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic alphabetMeaningTopic() {
        return TopicBuilder
                .name("alphabet-meaning")
                .partitions(3)
                .replicas(3)
                .compact()
                .build();
    }

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
