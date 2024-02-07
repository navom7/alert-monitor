package com.alertmonitor.config;


import com.alertmonitor.model.kafka.ClientEventDTODeserializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Value("${spring.kafka.retry.backoff.interval}")
    private long INTERVAL;

    @Value("${spring.kafka.retry.backoff.max-attempts}")
    private long MAX_ATTEMPTS;

    @Value("${spring.kafka.no-of-partitions}")
    private int NO_OF_PARTITIONS;

    @Value("${spring.kafka.client-event-partitioned-topic}")
    private String PARTITIONED_TOPIC;


    @Value("${spring.kafka.client-event-partitioned-group-id}")
    private String KAFKA_GROUP_ID;


    @Value("${spring.kafka.client-event-dlq-group-id}")
    private String KAFKA_DLQ_GROUP_ID;

    // Consumer Configuration
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_GROUP_ID);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ClientEventDTODeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    //topic is partitioned int configured value
    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(PARTITIONED_TOPIC).partitions(NO_OF_PARTITIONS).build();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1); // Set the desired concurrency level
        factory.getContainerProperties().setPollTimeout(3000); // Set the desired poll timeout
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }
    @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(INTERVAL, MAX_ATTEMPTS);
        return new DefaultErrorHandler((consumerRecord, exception) -> {
            // logic to execute when all the retry attemps are exhausted
        }, fixedBackOff);
    }
    @Bean
    public ConsumerFactory<String, String> dlqConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_DLQ_GROUP_ID);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> dlqKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(dlqConsumerFactory());
        factory.setConcurrency(3); // Set the desired concurrency level
        factory.getContainerProperties().setPollTimeout(3000); // Set the desired poll timeout
        return factory;
    }
}
