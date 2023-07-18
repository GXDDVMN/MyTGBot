package com.drobov.mytgbot.model.media_for_text;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoData {
    String center;
    @JsonProperty("data_created")
    LocalDateTime dateCreated;
    String description;
    String[] keywords;
    @JsonProperty("media_type")
    String mediaType;
    @JsonProperty("nasa_id")
    String nasaId;
    String title;

}
