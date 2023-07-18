package com.drobov.mytgbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class NasaConfig {
    @Value("${nasa.token}")
    private String token;
    @Value("${nasa.url}")
    private String url;
}
