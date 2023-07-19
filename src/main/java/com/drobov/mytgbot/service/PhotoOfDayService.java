package com.drobov.mytgbot.service;

import com.drobov.mytgbot.model.photo_of_day.PhotoNasa;
import com.drobov.mytgbot.nasa_api.NasaGetPhotoOfDay;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.Null;

@Service
@Log4j2
public class PhotoOfDayService {
    private final SendBotService sendBotService;

    private final NasaGetPhotoOfDay nasaGetPhotoOfDay;

    public PhotoOfDayService(SendBotService sendBotService, NasaGetPhotoOfDay nasaGetPhotoOfDay) {
        this.sendBotService = sendBotService;
        this.nasaGetPhotoOfDay = nasaGetPhotoOfDay;
    }

    private SendPhoto getPhotoOfDay(long chatId) throws NullPointerException {
        PhotoNasa response = nasaGetPhotoOfDay.getPhotoOfTheDay();
        String caption = null;
        try {
            caption = response.getExplanation().substring(0, 800);
        } catch (Exception e) {
            log.error(e);
        }
        if (caption == null) return null;
        caption = ("<b><u>" + response.getTitle() + "</u></b>\n"
                + caption.substring(0, caption.lastIndexOf('.')) + "\n<a href=\""
                + response.getHdurl() + "\">HD resolution</a>");
        SendPhoto reply = SendPhoto.builder()
                .parseMode("HTML")
                .caption(caption)
                .photo(new InputFile(response.getUrl()))
                .chatId(chatId)
                .build();
        return reply;
    }

    public void sendPhotoOfTheDay(CallbackQuery callbackQuery) {
        SendPhoto sendPhoto = getPhotoOfDay(callbackQuery.getMessage().getChatId());
        if (sendPhoto == null) {sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
            sendBotService.sendAnswerCallback(callbackQuery.getId(), "Failure");}
        else {
            try {
                sendBotService.sendMessage(sendPhoto);
                sendBotService.sendAnswerCallback(callbackQuery.getId(), "Success");
                log.info("success sending photo of the day to ChatID: " + callbackQuery.getMessage().getChat().getId());
            } catch (TelegramApiException e) {
                log.error("failed to send photo of the day", e);
                sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
            }
        }
    }
}

