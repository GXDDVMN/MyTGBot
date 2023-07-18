package com.drobov.mytgbot.model.rover_photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Camera{
    private long id;
    private String name;
    @JsonProperty("rover_id")
    private long roverId;
    @JsonProperty("full_name")
    private String fullName;
}