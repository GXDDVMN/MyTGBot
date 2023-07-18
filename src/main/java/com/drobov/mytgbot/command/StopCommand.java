package com.drobov.mytgbot.command;

import com.drobov.mytgbot.service.SendBotService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class StopCommand implements Command{

    public static final String STOP_MESSAGE = "Good luck";
    private final SendBotService sendBotService;

    public StopCommand(SendBotService sendBotService) {
        this.sendBotService = sendBotService;
    }


    @Override
    public void execute(Update update){
        sendStopMessage(update.getMessage().getChatId());
    }
    private void sendStopMessage(long chatId){
        try {
            sendBotService.sendMessage(chatId, STOP_MESSAGE);
            log.info("success reply with stop message to ChatID: " + chatId);
        }catch (TelegramApiException e){
            log.error("failed to reply with stop message");
            sendBotService.sendErrorMessage(chatId);
        }
    }
}
