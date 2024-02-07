package com.alertmonitor.service.impl;

import com.alertmonitor.model.kafka.ClientEventDTO;
import com.alertmonitor.service.MonitoringService;
import com.alertmonitor.service.alert.SimpleCountMonitoringService;
import com.alertmonitor.service.alert.SlidingWindowMonitoringService;
import com.alertmonitor.service.alert.TumblingWindowMonitoringService;
import com.alertmonitor.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MonitoringServiceImpl implements MonitoringService {


    private final SimpleCountMonitoringService simpleCountMonitoringService;
    private final TumblingWindowMonitoringService tumblingWindowMonitoringService;
    private final SlidingWindowMonitoringService slidingWindowMonitoringService;


    @Autowired
    public MonitoringServiceImpl(TumblingWindowMonitoringService tumblingWindowMonitoringService,
                                 SlidingWindowMonitoringService slidingWindowMonitoringService,
                                 SimpleCountMonitoringService simpleCountMonitoringService
    ) {
        this.simpleCountMonitoringService = simpleCountMonitoringService;
        this.tumblingWindowMonitoringService = tumblingWindowMonitoringService;
        this.slidingWindowMonitoringService = slidingWindowMonitoringService;
    }

    @Override
    public void monitor(ClientEventDTO clientEventDTO) throws JsonProcessingException {
        String alertType = Utils.getAlertType(clientEventDTO.getEventType());
        switch (alertType) {
            case "SIMPLE_COUNT":
                simpleCountMonitoringService.alert(clientEventDTO);
                break;
            case "TUMBLING_WINDOW":
                tumblingWindowMonitoringService.alert(clientEventDTO);
                break;
            case "SLIDING_WINDOW":
                slidingWindowMonitoringService.alert(clientEventDTO);
                break;
            default:
                //In case of alertType that is passed in the event is not present alerting in the console
                log.warn("Unknown alert type: " + alertType);
        }
    }
}
