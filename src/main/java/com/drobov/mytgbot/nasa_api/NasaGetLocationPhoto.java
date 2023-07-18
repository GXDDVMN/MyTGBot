package com.drobov.mytgbot.nasa_api;

import com.drobov.mytgbot.config.NasaConfig;
import com.drobov.mytgbot.model.location_photo.PhotoLocation;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Component
@Log4j2
public class NasaGetLocationPhoto {
    private final NasaConfig nasaConfig;

    public NasaGetLocationPhoto(NasaConfig nasaConfig) {
        this.nasaConfig = nasaConfig;
    }
    public PhotoLocation getPhotoLocation(String lat, String lon, LocalDate date){
        String url = nasaConfig.getUrl() + "/planetary/earth/assets";
        RestTemplate restTemplate = new RestTemplate();
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lon", lon)
                .queryParam("lat",lat)
                .queryParam("date", date.toString())
                .queryParam("dim", 0.2)
                .queryParam("api_key", nasaConfig.getToken())
                .build();
        ResponseEntity<PhotoLocation> response=null;
        try {
            response = restTemplate.getForEntity(builder.toUriString(), PhotoLocation.class);
        }catch (HttpClientErrorException e){
            log.error(e);
        }
        if(response==null) return getPhotoLocation(lat, lon, date.minusMonths(6));
        return response.getBody();
    }
}