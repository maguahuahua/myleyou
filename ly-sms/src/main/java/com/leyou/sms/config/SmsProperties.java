package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenxm
 * @date 2020/7/18 - 19:59
 */

@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {

    String accessKeyId;
    String accessKeySecret;
    String signName;
    String verifyCodeTemplate;
}
