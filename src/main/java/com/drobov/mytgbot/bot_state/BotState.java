package com.drobov.mytgbot.bot_state;

public enum BotState {
    WAITING_FOR_DATE_CURIOSITY ("curiosity"),
    WAITING_FOR_DATE_OPPORTUNITY ("opportunity"),
    WAITING_FOR_DATE_SPIRIT ("spirit"),
    WAITING_FOR_LOCATION("location"),
    WAITING_FOR_TEXT_VIDEO("textPhoto"),
    WAITING_FOR_TEXT_PHOTO("textVideo");
    private String value;

    BotState(String value) {
        this.value=value;
    }
    public String getValue(){
        return value;
    }
}
