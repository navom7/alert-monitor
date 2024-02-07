package com.alertmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisObject {

    private LinkedList<Long> timestampList;
}