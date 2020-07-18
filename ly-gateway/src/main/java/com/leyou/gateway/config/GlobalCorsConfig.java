package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author chenxm
 * @date 2020/7/2 - 11:55
 * cors拦截
 */

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        //添加cors配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许的域，不写*，否则cookie无法使用
        corsConfiguration.addAllowedOrigin("http://manager.leyou.com");
        corsConfiguration.addAllowedOrigin("http://www.leyou.com");
        //是否发送cookie
        corsConfiguration.setAllowCredentials(true);
        //允许的请求方式
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.addAllowedMethod("HEAD");
        corsConfiguration.addAllowedMethod("GET");
        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("PUT");
        corsConfiguration.addAllowedMethod("PATCH");
        corsConfiguration.addAllowedMethod("DELETE");
        //允许的头信息
        corsConfiguration.addAllowedHeader("*");
        //有效时长
        corsConfiguration.setMaxAge(3600L);
        //添加映射路径，拦截所有请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", corsConfiguration);

        //返回新的CorsFilter
        return new CorsFilter(configurationSource);
    }

}

