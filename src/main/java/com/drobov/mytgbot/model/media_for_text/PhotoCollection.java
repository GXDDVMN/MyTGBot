package com.drobov.mytgbot.model.media_for_text;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoCollection {
    private String version;
    private String href;
    private List<PhotoItems> items;
}
