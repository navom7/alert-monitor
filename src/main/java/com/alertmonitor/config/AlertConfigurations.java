package com.alertmonitor.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "alert")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertConfigurations {

    private List<AlertConfigList> alertConfigList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertConfigList {
        private String client;
        private String eventType;
        private AlertConfig alertConfig;
        private List<DispatchStrategy> dispatchStrategyList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertConfig {
        private String type;
        private int count;
        private int windowSizeInSecs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DispatchStrategy {
        private String type;
        private String message;
        private String subject; // Optional, as not all strategies have a subject
    }
}
