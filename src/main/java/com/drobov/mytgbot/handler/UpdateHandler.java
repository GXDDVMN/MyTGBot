package com.drobov.mytgbot.handler;

import com.drobov.mytgbot.bot_state.BotStateForChat;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j2
public class UpdateHandler {

    private final BotStateForChat botState;
    private final CallbackQueryHandler callbackQueryHandler;

    private final CommandHandler commandHandler;

    private final MessageHandler messageHandler;

    public UpdateHandler(BotStateForChat botState, CallbackQueryHandler callbackQueryHandler, CommandHandler commandHandler, MessageHandler messageHandler) {
        this.botState = botState;
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
    }


    public void handleUpdate(Update update){
        if(update.hasCallbackQuery()){
            log.info("New callbackQuery from ChatID: {} with data: {}", update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());
            callbackQueryHandler.processCallbackQuery(update.getCallbackQuery(), botState);
            return;
        }
        if(update.hasMessage() && update.getMessage().isCommand()){
            log.info("New command from ChatID: {} with command: {}", update.getMessage().getChatId(),
                    update.getMessage().getText());
            commandHandler.processCommand(update.getMessage()).execute(update);
            return;
        }
        if(update.hasMessage() && (update.getMessage().hasText()|update.getMessage().hasLocation())){
            log.info("New message from ChatID: {} with text: {}", update.getMessage().getChatId(),
                    update.getMessage().getText());
            messageHandler.processMessage(update.getMessage(), botState);
        }
    }
}
