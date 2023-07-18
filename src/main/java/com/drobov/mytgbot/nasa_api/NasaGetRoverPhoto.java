package com.drobov.mytgbot.nasa_api;

import com.drobov.mytgbot.config.NasaConfig;
import com.drobov.mytgbot.model.rover_photo.PhotoRover;
import com.drobov.mytgbot.model.rover_photo.Photos;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Component
@Log4j2
public class NasaGetRoverPhoto {

    private final NasaConfig nasaConfig;

    public NasaGetRoverPhoto(NasaConfig nasaConfig) {
        this.nasaConfig = nasaConfig;
    }

    public List<PhotoRover> getPhotos(String rover, LocalDate date) {
        String url = nasaConfig.getUrl() + "/mars-photos/api/v1/rovers/"+rover+"/photos";
        RestTemplate restTemplate = new RestTemplate();
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("earth_date", date)
                .queryParam("api_key", nasaConfig.getToken())
                .build();
        Photos response =restTemplate.getForObject(builder.toUriString(), Photos.class);
        if (response==null) return null;
        return response.getPhotos();
    }
}

