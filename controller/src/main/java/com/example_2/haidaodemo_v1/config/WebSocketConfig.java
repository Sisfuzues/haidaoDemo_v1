package com.example_2.haidaodemo_v1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        System.out.println("WebSocketConfig启动成功");
        return new ServerEndpointExporter();
    }
}
