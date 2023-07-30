package com.drobov.mytgbot;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyTgBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyTgBotApplication.class, args);
    }

}
