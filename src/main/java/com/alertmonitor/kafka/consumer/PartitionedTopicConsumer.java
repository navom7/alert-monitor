package com.alertmonitor.kafka.consumer;

import com.alertmonitor.model.kafka.ClientEventDTO;
import com.alertmonitor.service.MonitoringService;
import com.alertmonitor.utils.PublishToDLQ;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;


@Component
@Slf4j
//concurrency is set to 10 as there will be 10 parellel cosnumers to consume events
@KafkaListener(topics = "${spring.kafka.client-event-partitioned-topic}", groupId = "${spring.kafka.client-event-partitioned-group-id}", concurrency = "10")
public class PartitionedTopicConsumer {

    private final Logger logger = LoggerFactory.getLogger(PartitionedTopicConsumer.class);


    @Autowired
    private PublishToDLQ dlqPublisherUtil;

    @Autowired
    private MonitoringService monitoringService;


    @Autowired
    private ObjectMapper objectMapper;


    @KafkaHandler
    public void handleEvent(ClientEventDTO event) throws JsonProcessingException {
        try {

            monitoringService.monitor(event);

        } catch (HttpServerErrorException exception) {
            log.error("Error occurred :  Event :  {}  Clevertap threw : ", event, exception);
            throw new HttpServerErrorException(exception.getStatusCode());
        } catch(Exception ex) {
            log.error("Error occurred :  Event :  {} , Pushing to DLQ Exception : ", event, ex);
            dlqPublisherUtil.publishEventToDlq(event, objectMapper.writeValueAsString(event),
                    ex.getMessage());
        }
    }
}

