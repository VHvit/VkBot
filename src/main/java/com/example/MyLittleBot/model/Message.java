package com.example.MyLittleBot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Message {
    @JsonProperty("text")
    private String text;
    @JsonProperty("peer_id")
    private long peerId;
}