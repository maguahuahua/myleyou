package com.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenxm
 * @date 2020/7/22 - 15:08
 */

@Data
@ConfigurationProperties(prefix = "leyou.worker")
public class IdWorkerProperties {

    private long workerId;// 当前机器id

    private long dataCenterId;// 序列号

}