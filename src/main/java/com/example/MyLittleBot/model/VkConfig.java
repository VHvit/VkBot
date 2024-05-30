package com.example.MyLittleBot.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vk")
public class VkConfig {

    private Group group;
    private Access access;

    @Data
    public static class Group {
        private int id;
    }

    @Data
    public static class Access {
        private String token;
    }
}