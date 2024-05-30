package com.example.MyLittleBot.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vk")
public class VkConfig {

    private int groupId;
    private String accessToken;
}