package com.alertmonitor.model.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ClientEventDTODeserializer implements Deserializer {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            if (data == null){
//                log.info("Null byte data received at deserializing from the broker.");
                return null;
            }
//            log.info("Deserializing the byte data: "+ new String(data, StandardCharsets.UTF_8));

            return objectMapper.readValue(data, ClientEventDTO.class);
        } catch (Exception e) {
            log.error("Error Occured while deserializing the byte data:"+new String(data, StandardCharsets.UTF_8), e);
            // throw new SerializationException("Error when deserializing byte[] to GenericConsumerEvent");
            return data;
        }
    }
}

