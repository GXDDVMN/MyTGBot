package com.drobov.mytgbot.service;

import com.drobov.mytgbot.model.media_for_text.PhotoItems;
import com.drobov.mytgbot.nasa_api.NasaGetPhotoWithText;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class PhotoFromTextService {
    private final SendBotService sendBotService;
    private final NasaGetPhotoWithText nasaGetPhotoWithText;

    public PhotoFromTextService(SendBotService sendBotService, NasaGetPhotoWithText nasaGetPhotoWithText) {
        this.sendBotService = sendBotService;
        this.nasaGetPhotoWithText = nasaGetPhotoWithText;
    }

    public void sendMessageForText(CallbackQuery callbackQuery) {
        String text = "Please enter the keyword(s)";
        try {
            sendBotService.sendMessage(callbackQuery.getMessage().getChatId(), text);
        } catch (TelegramApiException e) {
            log.error("failed to reply with enter text message", e);
            sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
        }
        sendBotService.sendAnswerCallback(callbackQuery.getId(), StringUtils.capitalize(callbackQuery.getData()));
    }
    private void sendNotFoundMessage(long chatId){
        String text = "Media not found\n<i>Please repeat with the another keyword(s)</i>";
        try {
            sendBotService.sendMessage(chatId, text);
        } catch (TelegramApiException e) {
            log.error("failed to reply with enter text message", e);
            sendBotService.sendErrorMessage(chatId);
        }
    }

    public void sendPhoto(long chatId, String text, boolean video) {
        List<PhotoItems> photo = new ArrayList<>();
        try {
            photo = nasaGetPhotoWithText.getPhotoList(text.trim());
        } catch (IndexOutOfBoundsException e) {
            log.error("failed to find a media", e);
            sendNotFoundMessage(chatId);
        }
        if (!photo.isEmpty()) {
            if (video) {
                SendVideo message = getVideoMessage(chatId, photo);
                try {
                    sendBotService.sendMessage(message);
                    log.info("success sending video message to ChatID: "+chatId);
                } catch (TelegramApiException e) {
                    log.error("failed to reply with video from text message", e);
                    if (e.getMessage().contains("[400]")) sendVideoWithoutMedia(chatId, photo);
                    else sendBotService.sendErrorMessage(chatId);
                }
            } else {
                try {
                    sendBotService.sendMessage(getPhotoMessage(chatId, photo));
                    log.info("success sending photo message to ChatID: "+chatId);
                } catch (TelegramApiException e) {
                    log.error("failed to reply with photo from text message", e);
                    sendBotService.sendErrorMessage(chatId);
                }
            }
        } else sendNotFoundMessage(chatId);
    }

    private void sendVideoWithoutMedia(long chatId ,List<PhotoItems> media) {
        PhotoItems video = media.stream().filter(n -> n.getData()[0].getMediaType().equals("video")).findFirst().get();
        var response = nasaGetPhotoWithText.getMediaHref(video.getHref());
        String href = response.stream().filter(n->n.endsWith("mp4")).findFirst().get();
        SendMessage message = SendMessage.builder()
                .text("The size of media is too large for me. The link: <a href=\""+href+"\">" + video.getData()[0].getTitle()+"</a>")
                .parseMode("HTML")
                .chatId(chatId)
                .build();
        try {
            sendBotService.sendMessage(message);
        }catch (TelegramApiException e){
            log.error("failed to reply with url video message",e);
            sendBotService.sendErrorMessage(chatId);
        }
    }


    private SendPhoto getPhotoMessage(long chatId, List<PhotoItems> media) {
        PhotoItems photo = media.stream().filter(n -> n.getData()[0].getMediaType().equals("image")).findFirst().get();
        var response = nasaGetPhotoWithText.getMediaHref(photo.getHref());
        String href = response.stream().filter(n->n.endsWith("jpg")).skip(1).findFirst().get();
        SendPhoto message = SendPhoto.builder()
                .caption("<b>" + photo.getData()[0].getTitle() + "</b>\n" + photo.getData()[0].getDescription())
                .photo(new InputFile(href))
                .parseMode("HTML")
                .chatId(chatId)
                .build();
        return message;
    }

    private SendVideo getVideoMessage(long chatId, List<PhotoItems> media) {
        PhotoItems photo = media.stream().filter(n -> n.getData()[0].getMediaType().equals("video")).findFirst().orElse(null);
        if (photo==null) {
            sendNotFoundMessage(chatId);
            return null;
        }
        String url = photo.getLinks()[0].getHref().replaceAll("thumb", "mobile").replaceAll("jpg", "mp4");
        SendVideo sendVideo = SendVideo.builder()
                .chatId(chatId)
                .video(new InputFile(url))
                .caption("<b>" + photo.getData()[0].getTitle() + "</b>\n" + photo.getData()[0].getDescription())
                .parseMode("HTML")
                .build();
        return sendVideo;
    }

    public void sendMessageForSelect(CallbackQuery callbackQuery) {
        List<InlineKeyboardButton> buttonRow1 = new ArrayList<>();
        buttonRow1.add(InlineKeyboardButton.builder().text("Photo").callbackData("photo").build());
        buttonRow1.add(InlineKeyboardButton.builder().text("Video").callbackData("video").build());

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(buttonRow1)
                .build();

        SendMessage message = SendMessage.builder()
                .parseMode("HTML")
                .chatId(callbackQuery.getMessage().getChatId())
                .text("Please select the format")
                .replyMarkup(keyboard)
                .build();
        try {
            sendBotService.sendMessage(message);
            log.info("success sending select format message to ChatID: "+callbackQuery.getMessage().getChatId());
        }catch (TelegramApiException e){
            log.error("failed to reply with select format message", e);
            sendBotService.sendErrorMessage(callbackQuery.getMessage().getChatId());
        }
        sendBotService.sendAnswerCallback(callbackQuery.getId());
    }
}
