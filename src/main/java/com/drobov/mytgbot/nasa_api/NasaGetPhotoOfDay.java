package com.drobov.mytgbot.nasa_api;

import com.drobov.mytgbot.config.NasaConfig;
import com.drobov.mytgbot.model.photo_of_day.PhotoNasa;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.SocketTimeoutException;
import java.time.Duration;


@Log4j2
@Component
public class NasaGetPhotoOfDay {

    private final NasaConfig nasaConfig;

    public NasaGetPhotoOfDay(NasaConfig nasaConfig) {
        this.nasaConfig = nasaConfig;
    }

    public PhotoNasa getPhotoOfTheDay(){
        String url=nasaConfig.getUrl()+"/planetary/apod";
        //RestTemplate restTemplate = new RestTemplate();
        RestTemplate restTemplate2 = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("api_key", nasaConfig.getToken())
                .build();
        PhotoNasa response = null;
        try{
            response = restTemplate2.getForObject(builder.toUriString(), PhotoNasa.class);
        }catch (Exception e){
            log.error(e);
            return null;
        }
        return response;
    }
}
