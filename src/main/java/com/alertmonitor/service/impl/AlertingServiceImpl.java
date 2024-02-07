package com.alertmonitor.service.impl;

import com.alertmonitor.config.AlertConfigurations;
import com.alertmonitor.service.Alert;
import com.alertmonitor.service.AlertingService;
import com.alertmonitor.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertingServiceImpl implements AlertingService {

    private final EmailSenderService emailSenderService;
    private final Alert alert;

    @Autowired
    public AlertingServiceImpl(EmailSenderService emailSenderService, Alert alert) {
        this.emailSenderService = emailSenderService;
        this.alert = alert;
    }


    @Override
    public void alert(AlertConfigurations.AlertConfigList alertConfig) {
        alertConfig.getDispatchStrategyList().forEach(dispatchStrategy -> {
            if(dispatchStrategy.getType().equalsIgnoreCase("email")) {
                log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " Dispatching an Email");
                emailSenderService.sendEmail();
            } else if(dispatchStrategy.getType().equalsIgnoreCase("console")) {
                log.info("Client " + alertConfig.getClient() + " " + alertConfig.getEventType() + " Dispatching to Console");
                alert.alert(dispatchStrategy.getMessage());
            }
        });
    }
}
