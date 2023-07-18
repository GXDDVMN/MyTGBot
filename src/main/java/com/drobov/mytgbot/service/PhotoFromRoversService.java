package com.drobov.mytgbot.service;

import com.drobov.mytgbot.bot_state.BotState;
import com.drobov.mytgbot.bot_state.BotStateForChat;
import com.drobov.mytgbot.model.rover_photo.PhotoRover;
import com.drobov.mytgbot.nasa_api.NasaGetRoverPhoto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class PhotoFromRoversService {
    private final SendBotService sendBotService;
    private final NasaGetRoverPhoto nasaGetRoverPhoto;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final LocalDate START_SPIRIT = LocalDate.of(2004, 1,4);
    private static final LocalDate END_SPIRIT = LocalDate.of(2010,3,22);
    private static final LocalDate START_OPPORTUNITY = LocalDate.of(2004,1,25);
    private static final LocalDate END_OPPORTUNITY = LocalDate.of(2018,6,10);
    private static final LocalDate START_CURIOSITY = LocalDate.of(2012,8,6);

    public PhotoFromRoversService(SendBotService sendBotService, NasaGetRoverPhoto nasaGetRoverPhoto) {
        this.sendBotService = sendBotService;
        this.nasaGetRoverPhoto = nasaGetRoverPhoto;
    }


    public void sendPhotosFromRovers(Message message, BotStateForChat botState){
        LocalDate date = validateDateMessage(message.getChatId(), message,botState.getBotState(message.getChatId()));
        if (date!=null){
            List<PhotoRover> response = nasaGetRoverPhoto.getPhotos(botState.getBotState(message.getChatId()).getValue(), date);
            List<InputMedia> photos = getListOfMedia(response);
            try {
                sendBotService.sendMessage(SendMediaGroup.builder().chatId(message.getChatId()).medias(photos).build());
                log.info("success sending rovers photo message to ChatID: "+message.getChatId());
            }catch (TelegramApiException e){
                log.error("failed to reply with photos from rover",e);
                sendBotService.sendErrorMessage(message.getChatId());
            }
        }else sendIncorrectDateMessage(message.getChatId());
    }
    private List<InputMedia> getListOfMedia(List<PhotoRover> photos){
        List<InputMedia> media = new ArrayList<>();
        Map<String,PhotoRover> map = new HashMap<>();
        for (PhotoRover photo : photos) {
            map.put(photo.getCamera().getName(), photo);
        }
        for (PhotoRover photo: map.values()) {
            media.add(InputMediaPhoto.builder()
                            .media(photo.getImg())
                            .caption(photo.getCamera().getFullName()+"\n"+photo.getEarthDate())
                            .parseMode("HTML")
                    .build());
        }
        return media;
    }
    public void sendDateMessage(CallbackQuery callbackQuery, BotState botState){
        String text = getSelectDateTextMessage(botState);
        try {
            sendBotService.sendMessage(callbackQuery.getMessage().getChatId(), text);
            log.info("success sending select date message to ChatID: "+callbackQuery.getMessage().getChatId());
        }catch(TelegramApiException e){
            log.error("failed to reply with select date message",e);
            sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
        }
        sendBotService.sendAnswerCallback(callbackQuery.getId(), StringUtils.capitalize(callbackQuery.getData()));
    }
    private void sendIncorrectDateMessage(long chatId){
        String text ="<i>Please repeat with the correct date</i>";
        try {
            sendBotService.sendMessage(chatId, text);
            log.info("success sending select date message to ChatID: "+chatId);
        }catch(TelegramApiException e){
            log.error("failed to reply with select date message",e);
            sendBotService.sendErrorMessage(chatId);
        }
    }
    private LocalDate validateDateMessage(long chatId, Message message, BotState botState){
        String dateMessage = message.getText();
        LocalDate date = null;
        try{
            date = LocalDate.parse(dateMessage,formatter);
        }catch (DateTimeParseException e){
            log.error("failed to convert date", e);
            return null;
        }
        if (validateDate(date, botState)) return date;
        else return null;
    }
    private boolean validateDate(LocalDate date, BotState botState){
        switch (botState){
            case WAITING_FOR_DATE_CURIOSITY -> {return date.isAfter(START_CURIOSITY)&&date.isBefore(LocalDate.now());}
            case WAITING_FOR_DATE_SPIRIT -> {return date.isAfter(START_SPIRIT)&&date.isBefore(END_SPIRIT);}
            case WAITING_FOR_DATE_OPPORTUNITY -> {return date.isAfter(START_OPPORTUNITY)&&date.isBefore(END_OPPORTUNITY);}
        }
        return false;
    }



    private String getSelectDateTextMessage(BotState botState){
        String rangeDate=null;
        switch (botState){
            case WAITING_FOR_DATE_CURIOSITY -> {rangeDate = START_CURIOSITY.format(formatter)+" - "+LocalDate.now().format(formatter);}
            case WAITING_FOR_DATE_SPIRIT -> {rangeDate = START_SPIRIT.format(formatter)+" - "+END_SPIRIT.format(formatter);}
            case WAITING_FOR_DATE_OPPORTUNITY -> {rangeDate = START_OPPORTUNITY.format(formatter)+" - "+END_OPPORTUNITY.format(formatter);}
        }
        return "Please enter the date in the following range: \n<b>"+rangeDate+"</b>";
    }

    public void sendChooseRoverMessage(CallbackQuery callbackQuery) {
            sendChooseMessage(callbackQuery.getMessage().getChatId());
            sendBotService.sendAnswerCallback(callbackQuery.getId());
    }
    private void sendChooseMessage(Long chatId) {
        SendMessage selectRoverMessage = SendMessage
                .builder()
                .replyMarkup(getKeyboard())
                .parseMode("HTML")
                .text("<b>Please select the rover</b>")
                .chatId(chatId)
                .build();
        try {
            sendBotService.sendMessage(selectRoverMessage);
            log.info("success reply with select rover message to ChatID: "+chatId);
        }catch (TelegramApiException e){
            log.error("failed to reply with select rover message",e);
            sendBotService.sendErrorMessage(chatId);
        }
    }
    private InlineKeyboardMarkup getKeyboard(){
        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        buttonRow.add(InlineKeyboardButton.builder().text("Curiosity").callbackData("curiosity").build());
        buttonRow.add(InlineKeyboardButton.builder().text("Opportunity").callbackData("opportunity").build());
        buttonRow.add(InlineKeyboardButton.builder().text("Spirit").callbackData("spirit").build());
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(buttonRow)
                .build();
        return keyboard;
    }

}
