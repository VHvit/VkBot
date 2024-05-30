package com.example.MyLittleBot.service;

import com.example.MyLittleBot.model.VkConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class VkBotService {

    private final VkConfig callbackConfirmation;
    private final RestTemplate restTemplate = new RestTemplate();

    private String server;
    private String key;
    private String ts;

    @PostConstruct
    private void postConstruct() {
        initializeLongPollServer();
    }

    @Scheduled(fixedDelay = 1000)
    public void pollServer() {
        if (server == null || key == null || ts == null) {
            initializeLongPollServer();
        }

        String url = String.format("%s?act=a_check&key=%s&ts=%s&wait=25", server, key, ts);

        String response = restTemplate.getForObject(url, String.class);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

        ts = jsonObject.get("ts").getAsString();
        JsonArray updates = jsonObject.get("updates").getAsJsonArray();

        for (JsonElement updateElement : updates) {
            JsonObject update = updateElement.getAsJsonObject();
            if ("message_new".equals(update.get("type").getAsString())) {
                JsonObject message = update.getAsJsonObject("object");
                JsonElement fromIdElement = message.get("from_id");
                if (fromIdElement != null && !fromIdElement.isJsonNull()) {
                    int userId = fromIdElement.getAsInt();
                    String text = message.get("text").getAsString();
                    sendMessage(userId, "Вы сказали: " + text);
                }
            }
        }
    }

    private void initializeLongPollServer() {
        String url = String.format("https://api.vk.com/method/groups.getLongPollServer?group_id=%d&access_token=%s&v=5.131",
                callbackConfirmation.getGroup().getId(), callbackConfirmation.getAccess().getToken());
        String response = restTemplate.getForObject(url, String.class);

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("response");

        server = jsonObject.get("server").getAsString();
        key = jsonObject.get("key").getAsString();
        ts = jsonObject.get("ts").getAsString();
    }

    private void sendMessage(int userId, String message) {
        String url = "https://api.vk.com/method/messages.send";
        String params = String.format(
                "?user_id=%d&message=%s&random_id=%d&access_token=%s&v=5.131",
                userId, message, System.currentTimeMillis(), callbackConfirmation.getAccess().getToken());

        restTemplate.getForObject(url + params, String.class);
    }
}