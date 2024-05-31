package com.example.MyLittleBot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateObject {
    @JsonProperty("message")
    private Message message;
}