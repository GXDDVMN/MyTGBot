package com.drobov.mytgbot.handler;

import com.drobov.mytgbot.bot_state.BotState;
import com.drobov.mytgbot.bot_state.BotStateForChat;
import com.drobov.mytgbot.service.PhotoFromOrbitService;
import com.drobov.mytgbot.service.PhotoFromRoversService;
import com.drobov.mytgbot.service.PhotoFromTextService;
import com.drobov.mytgbot.service.PhotoOfDayService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;


@Component
public class CallbackQueryHandler {
    PhotoOfDayService photoOfDayService;
    PhotoFromRoversService photoFromRoversService;
    PhotoFromOrbitService photoFromOrbitService;

    private final PhotoFromTextService photoFromTextService;

    public CallbackQueryHandler(PhotoOfDayService photoOfDayService, PhotoFromRoversService photoFromRoversService, PhotoFromOrbitService photoFromOrbitService, PhotoFromTextService photoFromTextService) {
        this.photoOfDayService = photoOfDayService;
        this.photoFromRoversService = photoFromRoversService;
        this.photoFromOrbitService = photoFromOrbitService;
        this.photoFromTextService = photoFromTextService;
    }

    public void processCallbackQuery(CallbackQuery callbackQuery, BotStateForChat botState){
        if (callbackQuery.getData().equals("photo_of_day")){;
            photoOfDayService.sendPhotoOfTheDay(callbackQuery);
        }
        if (callbackQuery.getData().equals("rovers")){;
            photoFromRoversService.sendChooseRoverMessage(callbackQuery);
        }
        if (callbackQuery.getData().equals("orbit")){;
            photoFromOrbitService.sendMessageWithLocationRequest(callbackQuery);
            botState.putBotState(callbackQuery.getMessage().getChatId(),BotState.WAITING_FOR_LOCATION);
        }
        if (callbackQuery.getData().equals("nasa")){;
            photoFromTextService.sendMessageForSelect(callbackQuery);
        }
        if (("photo_video").contains(callbackQuery.getData())){;
            photoFromTextService.sendMessageForText(callbackQuery);
            if (callbackQuery.getData().equals("photo"))
                botState.putBotState(callbackQuery.getMessage().getChatId(),BotState.WAITING_FOR_TEXT_PHOTO);
            else botState.putBotState(callbackQuery.getMessage().getChatId(),BotState.WAITING_FOR_TEXT_VIDEO);
        }
        if (("curiosity_opportunity_spirit").contains(callbackQuery.getData())){
            if(callbackQuery.getData().equals("spirit"))
                botState.putBotState(callbackQuery.getMessage().getChatId(), BotState.WAITING_FOR_DATE_SPIRIT);
            else if (callbackQuery.getData().equals("curiosity"))
                botState.putBotState(callbackQuery.getMessage().getChatId(), BotState.WAITING_FOR_DATE_CURIOSITY);
            else
                botState.putBotState(callbackQuery.getMessage().getChatId(), BotState.WAITING_FOR_DATE_OPPORTUNITY);
            photoFromRoversService.sendDateMessage(callbackQuery, botState.getBotState(callbackQuery.getMessage().getChatId()));
        }
    }
}
