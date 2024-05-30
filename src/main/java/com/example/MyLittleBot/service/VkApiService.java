package com.example.MyLittleBot.service;

import com.example.MyLittleBot.model.VkConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class VkApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final VkConfig vkConfig;

    public JsonArray getUpdates(String server, String key, String ts) {
        String url = String.format("%s?act=a_check&key=%s&ts=%s&wait=25", server, key, ts);
        String response = restTemplate.getForObject(url, String.class);
        JsonElement jsonElement = JsonParser.parseString(response);
        return jsonElement.getAsJsonObject().getAsJsonArray("updates");
    }

    public JsonElement getLongPollServerResponse() {
        String url = String.format("https://api.vk.com/method/groups.getLongPollServer?group_id=%d&access_token=%s&v=5.131",
                vkConfig.getGroup().getId(), vkConfig.getAccess().getToken());
        String response = restTemplate.getForObject(url, String.class);
        return JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("response");
    }

    public void sendMessage(int userId, String message) {
        String url = "https://api.vk.com/method/messages.send";
        String params = String.format(
                "?user_id=%d&message=%s&random_id=%d&access_token=%s&v=5.131",
                userId, message, System.currentTimeMillis(), vkConfig.getAccess().getToken());
        restTemplate.getForObject(url + params, String.class);
    }
}