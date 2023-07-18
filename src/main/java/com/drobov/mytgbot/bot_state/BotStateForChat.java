package com.drobov.mytgbot.bot_state;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotStateForChat {
    private final Map<Long, BotState> botStateMap = new HashMap<>();
    public BotState getBotState(long chatId){
        return botStateMap.get(chatId);
    }
    public void putBotState(long chatId, BotState botState){
        botStateMap.put(chatId, botState);
    }
    public void deleteBotState(long chatId){
        botStateMap.remove(chatId);
    }
}
