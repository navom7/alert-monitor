package com.alertmonitor.service.alert;

import com.alertmonitor.config.AlertConfigurations;
import com.alertmonitor.model.kafka.ClientEventDTO;
import com.alertmonitor.service.AlertingService;
import com.alertmonitor.service.EmailSenderService;
import com.alertmonitor.utils.RedisUtility;
import com.alertmonitor.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.alertmonitor.constants.AppConstants.SIMPLE_COUNT_KEY_PREFIX;

@Slf4j
@EnableAsync
@Service
public class SimpleCountMonitoringService extends MonitoringAbstractService {

    private final AlertConfigurations alertConfigurations;
    private final RedisUtility redisUtility;
    private final AlertingService alertingService;

    @Autowired
    public SimpleCountMonitoringService(AlertConfigurations alertConfigurations,
                                          RedisUtility redisUtility,
                                          AlertingService alertingService) {
        this.redisUtility = redisUtility;
        this.alertConfigurations = alertConfigurations;
        this.alertingService = alertingService;
    }

    @Async
    @Override
    public void alert(ClientEventDTO clientEventDTO) throws JsonProcessingException {

        Optional<AlertConfigurations.AlertConfigList> optionalAlertConfig = alertConfigurations.getAlertConfigList().stream()
                .filter(config -> config.getEventType().equalsIgnoreCase(clientEventDTO.getEventType()))
                .findFirst();
        if (optionalAlertConfig.isPresent()) {
            AlertConfigurations.AlertConfigList alertConfig = optionalAlertConfig.get();

            String key = SIMPLE_COUNT_KEY_PREFIX + clientEventDTO.getEventType();
            Integer eventCount = redisUtility.getValue(key, Integer.class);

            if(Utils.hasValue(eventCount)) {
                if(eventCount >= alertConfig.getAlertConfig().getCount()) {
                    log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " threshold breached");
                    alertingService.alert(alertConfig);
                } else {
                    log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " " + alertConfig.getAlertConfig().getType());
                    eventCount += 1;
                    redisUtility.setValue(key, eventCount, 0);
                }
            } else {
                log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " " + alertConfig.getAlertConfig().getType());
                redisUtility.setValue(key, 1, 0);
            }
        } else {
            log.error("EventType not found!");
        }
    }




}
