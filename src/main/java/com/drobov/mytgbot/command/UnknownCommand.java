package com.drobov.mytgbot.command;

import com.drobov.mytgbot.service.SendBotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class UnknownCommand implements Command{
    public static final String UNKNOWN_MESSAGE = "I can't recognize the message, please enter /help to get the list of commands.";

    private final SendBotService sendBotService;

    public UnknownCommand(SendBotService sendBotService) {
        this.sendBotService = sendBotService;
    }

    @Override
    public void execute(Update update){
        sendUnknownCommandMessage(update.getMessage().getChatId());
    }
    private void sendUnknownCommandMessage(long chatId){
        try {
            sendBotService.sendMessage(chatId, UNKNOWN_MESSAGE);
            log.info("success reply with unknown message to ChatID: " + chatId);
        }catch (TelegramApiException e){
            log.error("failed to reply with unknown message",e);
            sendBotService.sendErrorMessage(chatId);
        }
    }
}
