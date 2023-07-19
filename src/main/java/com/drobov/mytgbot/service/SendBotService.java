package com.drobov.mytgbot.service;


import com.drobov.mytgbot.controller.TelegramBot;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Data
@Log4j2
@Service
public class SendBotService{
    private TelegramBot bot;
    public void registerBot(TelegramBot bot){
        this.bot=bot;
    }
    public void sendMessage(long chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);
        bot.execute(sendMessage);
    }
    public void sendMessage(SendPhoto sendPhoto) throws TelegramApiException {
        bot.execute(sendPhoto);
    }
    public void sendMessage(SendMessage sendMessage) throws TelegramApiException {
        bot.execute(sendMessage);
    }
    public void sendMessage(SendMediaGroup sendMediaGroup) throws TelegramApiException {
        bot.execute(sendMediaGroup);
    }
    public void sendMessage(SendLocation sendLocation) throws TelegramApiException {
        bot.execute(sendLocation);
    }
    public void sendMessage(SendVideo sendVideo) throws TelegramApiException {
        bot.execute(sendVideo);
    }
    public void sendAnswerCallback(String callbackQueryId, String message){
        try {
            bot.execute(AnswerCallbackQuery.builder().text(message).callbackQueryId(callbackQueryId).build());
        }catch (TelegramApiException e){
            log.error("failed to answer callback with id: "+callbackQueryId,e);
        }
    }
    public void sendAnswerCallback(String callbackQueryId){
        try {
            bot.execute(AnswerCallbackQuery.builder().callbackQueryId(callbackQueryId).build());
        }catch (TelegramApiException e){
            log.error("failed to answer callback with id: "+callbackQueryId,e);
        }
    }
    public void sendErrorMessage(long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Unexpected error. Please try again later. May be server is unavailable");
        try {
            bot.execute(sendMessage);
        }catch (TelegramApiException e){
            log.error("failed to reply with error message", e);
        }
    }
}