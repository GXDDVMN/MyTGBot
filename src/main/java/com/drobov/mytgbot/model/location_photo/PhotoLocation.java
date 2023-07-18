package com.drobov.mytgbot.model.location_photo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoLocation {
    private LocalDate date;
    private String id;
    private String url;
}
