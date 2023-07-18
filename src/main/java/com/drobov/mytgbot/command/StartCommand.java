package com.drobov.mytgbot.command;

import com.drobov.mytgbot.service.SendBotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class StartCommand implements Command{
    private final SendBotService sendBotService;

    public final static String START_MESSAGE = "Hello, I can help you to get some photos and other information from NASA\n<i>Please select the button below</i>";


    public StartCommand(SendBotService sendBotService) {
        this.sendBotService = sendBotService;
    }
    @Override
    public void execute(Update update) {
        sendStartMessage(update.getMessage().getChatId());
    }
    private InlineKeyboardMarkup getKeyboard(){
        List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
        List<InlineKeyboardButton> buttonRow2 = new ArrayList<>();
        buttonRow1.add(InlineKeyboardButton.builder().text("Photo of the day").callbackData("photo_of_day").build());
        buttonRow1.add(InlineKeyboardButton.builder().text("Photo from Mars rovers").callbackData("rovers").build());
        buttonRow2.add(InlineKeyboardButton.builder().text("Photo from orbit").callbackData("orbit").build());
        buttonRow2.add(InlineKeyboardButton.builder().text("Search in NASA").callbackData("nasa").build());
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(buttonRow1)
                .keyboardRow(buttonRow2)
                .build();
        return keyboard;
    }
    private void sendStartMessage(long chatId) {
        SendMessage startMessage = SendMessage
                .builder()
                .replyMarkup(getKeyboard())
                .parseMode("HTML")
                .text(START_MESSAGE)
                .chatId(chatId)
                .build();
        try {
            sendBotService.sendMessage(startMessage);
            log.info("success reply with start message to ChatID: "+chatId);
        }catch (TelegramApiException e){
            log.error("failed to reply with start message",e);
            sendBotService.sendErrorMessage(chatId);
        }
    }


}
