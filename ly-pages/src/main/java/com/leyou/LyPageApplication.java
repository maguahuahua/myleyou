package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.swing.*;

/**
 * @author chenxm
 * @date 2020/7/14 - 15:26
 */

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class LyPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyPageApplication.class);
    }
}
