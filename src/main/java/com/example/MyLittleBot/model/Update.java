package com.example.MyLittleBot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Update {
    @JsonProperty("type")
    private String type;
    @JsonProperty("object")
    private UpdateObject updateObject;
}