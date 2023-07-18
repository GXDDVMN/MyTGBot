package com.drobov.mytgbot.service;

import com.drobov.mytgbot.model.location_photo.PhotoLocation;
import com.drobov.mytgbot.nasa_api.NasaGetLocationPhoto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Log4j2
public class PhotoFromOrbitService {
    private final SendBotService sendBotService;
    private final NasaGetLocationPhoto nasaGetLocationPhoto;

    public PhotoFromOrbitService(SendBotService sendBotService, NasaGetLocationPhoto nasaGetLocationPhoto) {
        this.sendBotService = sendBotService;
        this.nasaGetLocationPhoto = nasaGetLocationPhoto;
    }

    public void sendMessageWithLocationRequest(CallbackQuery callbackQuery){
        String text = "Please enter coordinates (latitude longitude) or just send geolocation\nExample: 12.3456 65.4321";
        SendMessage message = SendMessage.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .parseMode("HTML")
                .text(text)
                .build();
        try {
            sendBotService.sendMessage(message);
            log.info("success sending location request message to ChatID: "+callbackQuery.getMessage().getChatId());
        }catch(TelegramApiException e){
            log.error("failed to reply with location request message", e);
            sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
        }
        sendBotService.sendAnswerCallback(callbackQuery.getId());
    }

    private void sendIncorrectMessage(long chatId){
        String text ="\n<i>Please enter the correct coordinates</i>";
        try {
            sendBotService.sendMessage(chatId, text);
            log.info("success sending location request message to ChatID: "+chatId);
        }catch(TelegramApiException e){
            log.error("failed to reply with location request message", e);
            sendBotService.sendErrorMessage(chatId);
        }
    }

    private void sendLocationPhoto(long chatId, PhotoLocation photo){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        SendPhoto message = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(photo.getUrl()))
                .caption(photo.getDate().format(formatter))
                .build();
        try {
            sendBotService.sendMessage(message);
            log.info("success sending photo location message to ChatID: "+chatId);
        }catch (TelegramApiException e){
            log.error("failed to reply with photo location message", e);
            sendBotService.sendErrorMessage(chatId);
        }
    }
    public void sendPhotoLocationMessage(Message message){
        String[] coords = getCoords(message);
        if (coords!=null){
            PhotoLocation response = nasaGetLocationPhoto.getPhotoLocation(coords[0],coords[1], LocalDate.now().minusYears(1));
            sendLocationPhoto(message.getChatId(), response);
        }else sendIncorrectMessage(message.getChatId());

    }
    private String[] getCoords(Message message){
        if (message.hasLocation()) return new String[]{message.getLocation().getLatitude().toString(),message.getLocation().getLongitude().toString()};
        String[] coords = message.getText().trim().split(" ");
        if (validateCoords(coords)) return coords;
        else return null;
    }
    private Boolean validateCoords(String[] coords){
        double lon;
        double lat;
        try {
            lat = Double.parseDouble(coords[0]);
            lon = Double.parseDouble(coords[1]);
            return inRange(lat,lon);
        }catch (Exception e){
            log.error("failed to parse coords",e);
            return false;
        }
    }
    private boolean inRange(double lat, double lon){
        return (lat < 90 && lat > -90 && lon < 180 && lon > -180);
    }

}
