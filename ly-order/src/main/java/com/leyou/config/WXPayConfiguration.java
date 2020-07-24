package com.leyou.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenxm
 * @date 2020/7/23 - 14:49
 */
@Configuration
public class WXPayConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "leyou.pay")
    public PayConfig payConfig() {
        return new PayConfig();
    }


    //交给spring管理
    @Bean
    public WXPay wxPay(PayConfig payConfig) {
        return new WXPay(payConfig, WXPayConstants.SignType.HMACSHA256);
    }
}
