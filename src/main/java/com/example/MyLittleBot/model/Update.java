package com.example.MyLittleBot.model;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class Update {
    private String type;
    private JsonObject object;
}
