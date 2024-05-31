package com.example.MyLittleBot.service;

import com.example.MyLittleBot.model.Message;
import com.example.MyLittleBot.model.Update;
import com.example.MyLittleBot.model.UpdateResponse;
import com.example.MyLittleBot.model.VkConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class VkBotService {

    private static final String VK_API_URL = "https://api.vk.com/method/";

    @Autowired
    private VkConfig vkConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String server;
    private String key;
    private String ts;

    private static final Logger logger = LoggerFactory.getLogger(VkBotService.class);

    @Scheduled(fixedDelay = 1000)
    public void pollServer() {
        if (server == null || key == null || ts == null) {
            initializeLongPollServer();
        }

        String url = String.format("%s?act=a_check&key=%s&ts=%s&wait=25", server, key, ts);
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            ts = jsonNode.get("ts").asText();

            JsonNode updates = jsonNode.get("updates");
            for (JsonNode updateNode : updates) {
                if ("message_new".equals(updateNode.get("type").asText())) {
                    JsonNode objectNode = updateNode.get("object");
                    if (objectNode != null && objectNode.has("from_id") && objectNode.has("text")) {
                        int userId = objectNode.get("from_id").asInt();
                        String text = objectNode.get("text").asText();
                        sendMessage(userId, "Вы сказали: " + text);
                    }
                }
            }
        } catch (RestClientException e) {
            logger.error("Error communicating with Long Poll Server", e);
            initializeLongPollServer(); // Попробовать повторно инициализировать Long Poll сервер
        } catch (Exception e) {
            logger.error("Error processing updates", e);
        }
    }

    private void initializeLongPollServer() {
        String url = String.format(VK_API_URL + "groups.getLongPollServer?group_id=%d&access_token=%s&v=5.131",
                vkConfig.getGroupId(), vkConfig.getAccessToken());
        String response = restTemplate.getForObject(url, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode responseNode = jsonNode.get("response");
            this.server = responseNode.get("server").asText();
            this.key = responseNode.get("key").asText();
            this.ts = responseNode.get("ts").asText();
        } catch (Exception e) {
            logger.error("Error initializing Long Poll Server", e);
        }
    }

    public void sendMessage(int userId, String message) {
        String url = String.format(VK_API_URL + "messages.send?user_id=%d&message=%s&random_id=%d&access_token=%s&v=5.131",
                userId, message, System.currentTimeMillis(), vkConfig.getAccessToken());
    }
}
