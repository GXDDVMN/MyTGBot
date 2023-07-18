package com.drobov.mytgbot.model.rover_photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Rover{
    private long id;
    private String name;
    @JsonProperty("landing_date")
    private LocalDate landingDate;
    @JsonProperty("launch_date")
    private LocalDate launchDate;
    private String status;
}