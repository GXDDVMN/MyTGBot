package com.drobov.mytgbot.handler;

import com.drobov.mytgbot.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler {
    HelpCommand helpCommand;
    UnknownCommand unknownCommand;
    StartCommand startCommand;
    StopCommand stopCommand;


    public CommandHandler(HelpCommand helpCommand, UnknownCommand unknownCommand, StartCommand startCommand, StopCommand stopCommand) {
        this.helpCommand = helpCommand;
        this.unknownCommand = unknownCommand;
        this.startCommand = startCommand;
        this.stopCommand = stopCommand;
    }

    public Command processCommand(Message message){
        switch (message.getText()){
            case "/start" -> { return startCommand; }
            case "/help" -> { return helpCommand; }
            case "/stop" -> { return stopCommand; }
            default -> { return unknownCommand; }
        }
    }
}
