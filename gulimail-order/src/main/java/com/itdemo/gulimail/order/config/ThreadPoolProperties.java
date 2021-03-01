package com.itdemo.gulimail.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "gulimail-thread")
@Data
public class ThreadPoolProperties {
    private Integer core;
    private Integer maxPool;
    private Integer time;
}
