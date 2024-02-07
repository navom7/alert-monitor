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

import static com.alertmonitor.constants.AppConstants.TUMBLING_WINDOW_KEY_PREFIX;

@Service
@EnableAsync
@Slf4j
public class TumblingWindowMonitoringService extends MonitoringAbstractService {
    private final AlertConfigurations alertConfigurations;
    private final RedisUtility redisUtility;
    private final AlertingService alertingService;

    @Autowired
    public TumblingWindowMonitoringService(AlertConfigurations alertConfigurations,
                                        RedisUtility redisUtility,
                                       AlertingService alertingService
    ) {
        this.redisUtility = redisUtility;
        this.alertConfigurations = alertConfigurations;
        this.alertingService = alertingService;
    }

    @Override
    @Async
    public void alert(ClientEventDTO clientEventDTO) throws JsonProcessingException {

        Optional<AlertConfigurations.AlertConfigList> optionalAlertConfig = alertConfigurations.getAlertConfigList().stream()
                .filter(config -> config.getEventType().equalsIgnoreCase(clientEventDTO.getEventType()))
                .findFirst();

        if (optionalAlertConfig.isPresent()) {
            AlertConfigurations.AlertConfigList alertConfig = optionalAlertConfig.get();

            String key = TUMBLING_WINDOW_KEY_PREFIX + clientEventDTO.getEventType();
            Integer eventCount = redisUtility.getValue(key, Integer.class);

            long nextTimeout = Utils.getNextEpochTimeForTumblingWindow(alertConfig.getAlertConfig().getWindowSizeInSecs());

            if(Utils.hasValue(eventCount)) {
                if(eventCount >= alertConfig.getAlertConfig().getCount()) {
                    log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " threshold breached");
                    alertingService.alert(alertConfig);
                } else {
                    log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " " + alertConfig.getAlertConfig().getType());
                    redisUtility.setValue(key, eventCount+1, nextTimeout);
                }
            } else {
                log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " " + alertConfig.getAlertConfig().getType());
                redisUtility.setValue(key, 1, nextTimeout);
            }
        } else {
            log.error("EventType not found!");
        }
    }


}
