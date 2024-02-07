package com.alertmonitor.controller;


import com.alertmonitor.kafka.publisher.KafkaPublisherService;
import com.alertmonitor.model.kafka.ClientEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaController {

    @Autowired
    private KafkaPublisherService producerService;

    @PostMapping("/publish/{topic}")
    public void sendMessageToKafkaTopic(@RequestBody ClientEventDTO clientEventDTO, @PathVariable("topic") String topic) {
        producerService.sendEventToMonitoringService(topic, clientEventDTO);
    }
}
