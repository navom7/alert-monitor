package com.alertmonitor.service.impl;

import com.alertmonitor.service.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AlertImpl implements Alert {

    @Override
    public void alert(String message) {
        log.warn(message);
    }
}
