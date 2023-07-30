package com.drobov.mytgbot.ping_task;

import com.drobov.mytgbot.model.location_photo.PhotoLocation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
@Slf4j
@Getter
@Setter
public class PingTask {
    @Value("${pingtask.url}")
    private String url;

    @Scheduled(fixedRateString = "${pingtask.period}")
    public void pingMe() {
        RestTemplate restTemplate = new RestTemplate();
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url).build();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity(builder.toUriString(), String.class);
            log.info("Ping");
        } catch (RestClientException e) {
            log.error("Ping FAILED", e);
        }
    }

}