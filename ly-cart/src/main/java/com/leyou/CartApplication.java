package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author chenxm
 * @date 2020/7/21 - 16:33
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class);
    }
}