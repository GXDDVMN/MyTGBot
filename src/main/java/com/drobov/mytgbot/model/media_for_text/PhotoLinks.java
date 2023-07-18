package com.drobov.mytgbot.model.media_for_text;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoLinks {
    private String href;
    private String rel;
    private String render;
}
