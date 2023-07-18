package com.drobov.mytgbot.controller;

import com.drobov.mytgbot.handler.UpdateHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final UpdateHandler updateHandler;

    public WebHookController(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }
    @PostMapping(value = "/callback/update")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update){
        updateHandler.handleUpdate(update);
        return ResponseEntity.ok().build();
    }
}
