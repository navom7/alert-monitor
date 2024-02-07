package com.alertmonitor.model.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ClientEventDTO implements Serializable {

    private String eventType;
    private Long epochTime;

    @Override
    public String toString() {
        return "ClientEventDTO{" +
                ", eventType='" + eventType + '\'' +
                ", epochTime=" + epochTime +
                '}';
    }
}
