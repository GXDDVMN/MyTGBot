package com.drobov.mytgbot.model.media_for_text;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    PhotoCollection collection;
}
