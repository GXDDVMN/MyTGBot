package com.drobov.mytgbot.command;

import com.drobov.mytgbot.service.SendBotService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class HelpCommand implements Command {

    private final SendBotService sendBotService;

    static final String HELP_MESSAGE = String.format("""
                          <u><b>Available commands</b></u>

                    %s - Main menu of the bot
                    %s - Stop communicating with the bot
                    %s - List of available commands
                    """,
            "/start", "/stop", "/help");

    public HelpCommand(SendBotService sendBotService) {
        this.sendBotService = sendBotService;
    }

    @Override
    public void execute(Update update){
        sendHelpMessage(update.getMessage().getChatId());
    }
    private void sendHelpMessage(long chatId){
        try {
            sendBotService.sendMessage(chatId, HELP_MESSAGE);
            log.info("success reply with help message to ChatID: " + chatId);
        }catch (TelegramApiException e){
            log.error("failed to reply with help message");
            sendBotService.sendErrorMessage(chatId);
        }
    }
}

