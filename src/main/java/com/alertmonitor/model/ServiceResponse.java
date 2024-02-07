package com.alertmonitor.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse<T> {
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    private final String status;
    private final String message;
    private final T data;
}
