package com.example.MyLittleBot.service;

import com.example.MyLittleBot.model.VkConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class VkBotService {

    private final VkApiService vkApiService;
    private final RestTemplate restTemplate = new RestTemplate();

    private String server;
    private String key;
    private String ts;

    @Scheduled(fixedDelay = 1000)
    public void pollServer() {
        if (server == null || key == null || ts == null) {
            initializeLongPollServer();
        }

        String url = String.format("%s?act=a_check&key=%s&ts=%s&wait=25", server, key, ts);

        String response = restTemplate.getForObject(url, String.class);

        JsonElement jsonElement = JsonParser.parseString(response);
        String newTs = jsonElement.getAsJsonObject().get("ts").getAsString(); // Обновляем значение ts

        JsonArray updates = jsonElement.getAsJsonObject().getAsJsonArray("updates");

        for (JsonElement updateElement : updates) {
            JsonElement update = updateElement.getAsJsonObject();
            if ("message_new".equals(update.getAsJsonObject().get("type").getAsString())) {
                JsonElement message = update.getAsJsonObject().get("object");
                JsonElement fromIdElement = message.getAsJsonObject().get("from_id");
                if (fromIdElement != null && !fromIdElement.isJsonNull()) {
                    int userId = fromIdElement.getAsInt();
                    String text = message.getAsJsonObject().get("text").getAsString();
                    vkApiService.sendMessage(userId, "Вы сказали: " + text);
                }
            }
        }

        ts = newTs;
    }

    private void initializeLongPollServer() {
        JsonElement longPollServerResponse = vkApiService.getLongPollServerResponse();
        this.server = longPollServerResponse.getAsJsonObject().get("server").getAsString();
        this.key = longPollServerResponse.getAsJsonObject().get("key").getAsString();
        this.ts = longPollServerResponse.getAsJsonObject().get("ts").getAsString();
    }
}