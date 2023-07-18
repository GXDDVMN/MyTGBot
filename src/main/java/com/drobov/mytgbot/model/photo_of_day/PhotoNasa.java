package com.drobov.mytgbot.model.photo_of_day;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
public class PhotoNasa {
    private String copyright;
    private String date;
    private String explanation;
    @JsonProperty("media_type")
    private String mediaType;
    private String hdurl;
    private String url;
    private String title;
    @JsonProperty("service_version")
    private String serviceVersion;
}
