package com.alertmonitor.utils;

import com.alertmonitor.kafka.publisher.KafkaPublisherService;
import com.alertmonitor.model.kafka.ClientEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublishToDLQ {

    private final KafkaPublisherService kafkaPublisherService;


    @Value("${spring.kafka.dlq-topic}")
    private String DLQ_TOPIC;


    @Autowired
    public PublishToDLQ(KafkaPublisherService kafkaPublisherService) {
        this.kafkaPublisherService = kafkaPublisherService;
    }
    public void publishEventToDlq(Object consumerEvent, String errorMessage, String summary) throws JsonProcessingException {
        kafkaPublisherService.sendMessageToDLQTopic(new ClientEventDTO());
    }
}
