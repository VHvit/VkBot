package com.example.MyLittleBot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VKUpdates {

    @JsonProperty("ts")
    private String ts;

    @JsonProperty("updates")
    private List<Update> updates;
}
