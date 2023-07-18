package com.drobov.mytgbot.controller;


import com.drobov.mytgbot.handler.UpdateHandler;
import com.drobov.mytgbot.service.SendBotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
@Log4j2

public class TelegramBot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.uri}")
    private String botUri;

    private final SendBotService sendBotService;
    private final UpdateHandler updateHandler;

    public TelegramBot(SendBotService sendBotService, UpdateHandler updateHandler) {
        this.sendBotService = sendBotService;
        this.updateHandler = updateHandler;
    }

    @PostConstruct
    public void init() {
        SetMyCommands set = SetMyCommands.builder()
                .command(new BotCommand("/start", "Start working with bot"))
                .command(new BotCommand("/help", "Show command list"))
                .command(new BotCommand("/stop", "Stopping the bot"))
                .scope(new BotCommandScopeDefault())
                .languageCode(null)
                .build();
        try {
            this.execute(set);
        } catch (TelegramApiException e) {
            log.error(e);
        }
        sendBotService.registerBot(this);
        try{
            var setWebHook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(setWebHook);
        }catch(TelegramApiException e){
            log.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }
}