package com.example_2.haidaodemo_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@EnableScheduling
@SpringBootApplication
@MapperScan("com.example_2.haidaodemo_v1.mapper")
@ComponentScan("com.example_2.haidaodemo_v1")
public class HaidaoDemoV1Application {
    public static void main(String[] args) {
        SpringApplication.run(HaidaoDemoV1Application.class, args);
        System.out.println("===============");
        System.out.println("    正在测试    ");
        System.out.println("===============");
    }

}
