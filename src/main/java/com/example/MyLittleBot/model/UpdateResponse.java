package com.example.MyLittleBot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateResponse {
    @JsonProperty("ts")
    private String timestamp;
    @JsonProperty("updates")
    private List<Update> updates;
}