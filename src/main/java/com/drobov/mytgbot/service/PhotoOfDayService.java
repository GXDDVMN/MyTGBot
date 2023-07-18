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

@Service
@Log4j2
public class PhotoOfDayService {
    private final SendBotService sendBotService;

    private final NasaGetPhotoOfDay nasaGetPhotoOfDay;

    public PhotoOfDayService(SendBotService sendBotService, NasaGetPhotoOfDay nasaGetPhotoOfDay) {
        this.sendBotService = sendBotService;
        this.nasaGetPhotoOfDay = nasaGetPhotoOfDay;
    }

    private SendPhoto getPhotoOfDay(long chatId) {
        PhotoNasa response = nasaGetPhotoOfDay.getPhotoOfTheDay();
        String caption = response.getExplanation().substring(0,800);
        caption = ("<b><u>" + response.getTitle() + "</u></b>\n"
                + caption.substring(0,caption.lastIndexOf('.')) + "\n<a href=\""
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
        try {
            sendBotService.sendMessage(getPhotoOfDay(callbackQuery.getMessage().getChatId()));
            sendBotService.sendAnswerCallback(callbackQuery.getId(), "Success");
            log.info("success sending photo of the day to ChatID: "+callbackQuery.getMessage().getChat().getId());
        } catch (TelegramApiException e) {
            log.error("failed to send photo of the day",e);
            sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
        }
    }
}

