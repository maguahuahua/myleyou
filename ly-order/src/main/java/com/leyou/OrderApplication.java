package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author chenxm
 * @date 2020/7/22 - 10:08
 */


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.leyou.mapper")
public class OrderApplication {
    public static void main(String[] args) {

        SpringApplication.run(OrderApplication.class, args);
    }
}
