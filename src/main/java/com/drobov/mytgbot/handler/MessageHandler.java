package com.drobov.mytgbot.handler;

import com.drobov.mytgbot.bot_state.BotState;
import com.drobov.mytgbot.bot_state.BotStateForChat;
import com.drobov.mytgbot.service.PhotoFromOrbitService;
import com.drobov.mytgbot.service.PhotoFromRoversService;
import com.drobov.mytgbot.service.PhotoFromTextService;
import com.drobov.mytgbot.service.SendBotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class MessageHandler {
    private final SendBotService sendBotService;
    private final PhotoFromRoversService photoFromRoversService;

    private final PhotoFromOrbitService photoFromOrbitService;
    private final PhotoFromTextService photoFromTextService;

    public MessageHandler(SendBotService sendBotService, PhotoFromRoversService photoFromRoversService, PhotoFromOrbitService photoFromOrbitService, PhotoFromTextService photoFromTextService) {
        this.sendBotService = sendBotService;
        this.photoFromRoversService = photoFromRoversService;
        this.photoFromOrbitService = photoFromOrbitService;
        this.photoFromTextService = photoFromTextService;
    }

    public void processMessage(Message message, BotStateForChat botState) {
        if (botState.getBotState(message.getChatId()) == null) {
            try {
                sendBotService.sendMessage(message.getChatId(), "I don't understand you, please enter /help for more information");
            } catch (TelegramApiException e) {
                log.error("failed to reply with error message", e);
            }
        } else {
            if (("curiosity_opportunity_spirit").contains(botState.getBotState(message.getChatId()).getValue())) {
                photoFromRoversService.sendPhotosFromRovers(message, botState);
            }
            if (botState.getBotState(message.getChatId()) == BotState.WAITING_FOR_LOCATION) {
                photoFromOrbitService.sendPhotoLocationMessage(message);
            }
            if (botState.getBotState(message.getChatId()) == BotState.WAITING_FOR_TEXT_PHOTO) {
                photoFromTextService.sendPhoto(message.getChatId(), message.getText(), false);
            }
            if (botState.getBotState(message.getChatId()) == BotState.WAITING_FOR_TEXT_VIDEO) {
                photoFromTextService.sendPhoto(message.getChatId(), message.getText(), true);
            }
        }
    }
}
