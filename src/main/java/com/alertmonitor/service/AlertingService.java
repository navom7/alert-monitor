package com.alertmonitor.service;

import com.alertmonitor.config.AlertConfigurations;
import org.springframework.stereotype.Service;

@Service
public interface AlertingService {
    void alert(AlertConfigurations.AlertConfigList alertConfig);
}
