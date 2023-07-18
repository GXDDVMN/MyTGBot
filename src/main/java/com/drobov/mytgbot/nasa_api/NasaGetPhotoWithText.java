package com.drobov.mytgbot.nasa_api;

import com.drobov.mytgbot.config.NasaConfig;
import com.drobov.mytgbot.model.media_for_text.PhotoItems;
import com.drobov.mytgbot.model.media_for_text.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class NasaGetPhotoWithText {
    private final NasaConfig nasaConfig;

    public NasaGetPhotoWithText(NasaConfig nasaConfig) {
        this.nasaConfig = nasaConfig;
    }

    public List<PhotoItems> getPhotoList(String search){
        String uri = "https://images-api.nasa.gov/search";
        RestTemplate restTemplate = new RestTemplate();
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("q", search)
                .build();
        Response response=null;
        try {
            response = restTemplate.getForObject(builder.toUriString(), Response.class);
        }catch (HttpClientErrorException e){
            log.error(e);
        }
        if (response==null) return null;
        return response.getCollection().getItems();
    }
    public List<String> getMediaHref(String uri){
        var response = new RestTemplate().getForObject(uri, String[].class);
        if (response==null) return null;
        return Arrays.stream(response).toList();
    }
}