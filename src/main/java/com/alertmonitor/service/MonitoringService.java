package com.alertmonitor.service;

import com.alertmonitor.model.kafka.ClientEventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public interface MonitoringService {
    void monitor(ClientEventDTO clientEventDTO) throws JsonProcessingException;
}
