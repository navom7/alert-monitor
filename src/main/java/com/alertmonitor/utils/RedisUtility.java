package com.alertmonitor.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.alertmonitor.constants.AppConstants.REDIS_TIMEOUT_HOURS;


@Service
@Component
@Slf4j
public class RedisUtility {


    private RedisTemplate<String, String> redisTemplate;

    private ObjectMapper objectMapper;

    private TypeFactory _typeFactory;

    @Autowired
    public RedisUtility(ObjectMapper objectMapper, RedisTemplate<String, String> redisTemplate) {
        this._typeFactory = objectMapper.getTypeFactory();
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }


    public void setValue(String key, Object value, long timeout)
    {
        try{
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
            timeout = Utils.hasValue(timeout) && timeout != 0 ? timeout : 3600*24;
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        } catch (JsonProcessingException ex) {
            log.error("ERROR: While writing data to redis: " + ex.getMessage(), ex);
            throw new IllegalStateException();
        }
    }


    public <T> T getValue(String key, Class<T> valueType) throws JsonProcessingException {
        try{
            var result = redisTemplate.opsForValue().get(key);
            if(result == null) {
                return null;
            }
            return objectMapper.readValue(result,  _typeFactory.constructType(valueType));
        } catch (Exception ex) {
            log.error("ERROR: While reading data from Redis: " + ex.getMessage(), ex);
            return null;
        }
    }

    public void deleteKeyFromRedis(String key) {
        redisTemplate.delete(key);
    }

}
