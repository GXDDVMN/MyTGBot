package com.drobov.mytgbot.model.rover_photo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PhotoRover {
    private long id;
    private long sol;
    private Camera camera;
    @JsonProperty("img_src")
    private String img;
    @JsonProperty("earth_date")
    private LocalDate earthDate;
    private Rover rover;



}
