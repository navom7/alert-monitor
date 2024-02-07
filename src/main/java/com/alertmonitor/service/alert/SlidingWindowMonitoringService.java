package com.alertmonitor.service.alert;

import com.alertmonitor.config.AlertConfigurations;
import com.alertmonitor.model.RedisObject;
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

import static com.alertmonitor.constants.AppConstants.SLIDING_WINDOW_KEY_PREFIX;

@Service
@EnableAsync
@Slf4j
public class SlidingWindowMonitoringService extends MonitoringAbstractService {
    private final AlertConfigurations alertConfigurations;
    private final RedisUtility redisUtility;
    private final AlertingService alertingService;

    @Autowired
    public SlidingWindowMonitoringService(AlertConfigurations alertConfigurations,
                                           RedisUtility redisUtility,
                                           AlertingService alertingService) {
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

            String key = SLIDING_WINDOW_KEY_PREFIX + clientEventDTO.getEventType() + "client_id";
            RedisObject redisObject = redisUtility.getValue(key, RedisObject.class);

            long allowedTimeWindow = Utils.getTimeForAllowedTimeWindow(alertConfig.getAlertConfig().getWindowSizeInSecs());

            if(Utils.hasValue(redisObject) && Utils.hasValue(redisObject.getTimestampList())) {
                RedisObject updateRedisObject = getUpdatedRedisItemList(redisObject, allowedTimeWindow);

                if(updateRedisObject.getTimestampList().size() >= alertConfig.getAlertConfig().getCount()) {
                    log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " threshold breached");
                    alertingService.alert(alertConfig);
                } else {
                    log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " " + alertConfig.getAlertConfig().getType());
                    updateRedisObject.getTimestampList().add(System.currentTimeMillis()/1000);
                    redisUtility.setValue(key, updateRedisObject, 0);
                }
            } else {
                log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " " + alertConfig.getAlertConfig().getType());
                LinkedList<Long> redisObjectItems = new LinkedList<>();
                redisObjectItems.add(System.currentTimeMillis()/1000);
                RedisObject newRedisObject = new RedisObject(redisObjectItems);
                redisUtility.setValue(key, newRedisObject, 0);
            }
        } else {
            log.error("EventType not found!");
        }
    }

    //this function deletes all the timestamps that are out of allowed time window and then return the reamining object
    RedisObject getUpdatedRedisItemList(RedisObject redisObject, long allowedTimeWindowStart) {
        LinkedList<Long> timestampItems = redisObject.getTimestampList();

        Iterator<Long> iterator = timestampItems.iterator();
        while(iterator.hasNext()) {
            Long currentTimestamp = iterator.next();
            if(currentTimestamp < allowedTimeWindowStart) {
                iterator.remove();
            } else {
                break;
            }
        }
        return new RedisObject(timestampItems);
    }
}
