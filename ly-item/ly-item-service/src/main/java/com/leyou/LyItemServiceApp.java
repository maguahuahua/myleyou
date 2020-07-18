package com.leyou;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.ControllerAdvice;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author chenxm
 * @date 2020/6/29 - 17:42
 */


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper")
public class LyItemServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(LyItemServiceApp.class);
    }
}
