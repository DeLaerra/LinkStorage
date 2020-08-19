package com.innopolis.referencestorage.util.TelegramBot;

import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class LinkStorageBot extends TelegramLongPollingBot {
    static {
        ApiContextInitializer.init();
    }

    private UserService userService;
    private UserInfoService userInfoService;
    private PrivateMessageService privateMessageService;
    private ReferenceService referenceService;
    private FriendsService friendsService;
    private Map<Long, String> recipients = new HashMap<>();


    @Value("1382682447:AAF5lYS10l43ZNgrXuEwm2qCnArQpEfAy_M")
    private String token;

    @Value("link_storage_bot")
    private String botUsername;


    @Autowired
    public LinkStorageBot(UserService userService, UserInfoService userInfoService,
                          PrivateMessageService privateMessageService, ReferenceService referenceService,
                          FriendsService friendsService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
        this.privateMessageService = privateMessageService;
        this.referenceService = referenceService;
        this.friendsService = friendsService;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Новый апдейт получен. UpdateID: " + update.getUpdateId());
        SendMessage response = null;
        SendMessage badResponse = getBadResponseMessage(update.getMessage().getChatId());

        if (update.hasMessage()) {
            Message message = update.getMessage();
            log.info("Получено \"{}\" от {}", update.getMessage().getText(), update.getMessage().getChatId());
            response = parseCommands(message, badResponse);
        } else {
            response = badResponse;
        }

        try {
            execute(response);
            log.info("Отправлено \"{}\" пользователю {}", response.getText(), response.getChatId());
        } catch (TelegramApiException e) {
            log.error("Сообщение \"{}\" пользователю {} не отправлено из-за ошибки: {}",
                    response.getText(), response.getChatId(), e.getMessage());
        }
    }

    private SendMessage getBadResponseMessage(Long chatId) {
        return new SendMessage()
                .setChatId(chatId)
                .setText("Это не ссылка!");
    }


    private SendMessage parseCommands(Message message, SendMessage badResponse) {
        SendMessage response;
        Long chatId = message.getChatId();
        String messageText = message.getText();
        String username = recipients.get(chatId);

        if (message.hasText()) {
            if (messageText.equals("/start")) {
                response = new SendMessage()
                        .setChatId(chatId)
                        .setText("Здравствуйте! Ваш chatId:" + "\n"
                                + chatId + "\n"
                                + "chatID необходимо сохранить в Личном кабинете." + "\n"
                                + "Пожалуйста, укажите никнейм пользователя, которому вы хотите отправлять ссылки");
            } else if (messageText.startsWith("/to")) {
                String nickname = messageText.replace("/to ", "");
                response = getUserNicknameMessage(chatId, nickname);
            } else if (messageText.startsWith("/help")) {
                response = new SendMessage()
                        .setChatId(chatId)
                        .setText("Перед отправкой ссылок необходимо сохранить свой chatId в Личном кабинете, а также отправить боту сообщение с ником адресата." + "\n" +
                                "Для смены адресата отправьте команду /to и никнейм нового адресата. Например, /to admin");
            } else {
                response = getURLFromMessage(message, badResponse, username);
            }

        } else {
            response = badResponse;
            if (message.getCaptionEntities() != null) {
                for (MessageEntity messageEntity : message.getCaptionEntities()) {
                    log.info(messageEntity.toString());
                    try {
                        URL url = new URL(messageEntity.getText());
                        log.info("Обнаружена ссылка во вложении" + url);

                        response = new SendMessage()
                                .setChatId(message.getChatId())
                                .setText("Ссылка из вложения отправлена пользователю " + username);
                    } catch (MalformedURLException ex) {
                        log.warn("Во вложении нет ссылки!");
                    }
                }
            }
        }

        return response;
    }


    private SendMessage getURLFromMessage(Message message, SendMessage badResponse, String username) {
        URL url;
        SendMessage response;
        Long chatId = message.getChatId();
        String messageText = message.getText();


        if (username != null) {
            response = badResponse;

            // separate input by spaces (URLs don't have spaces)
            String[] words = messageText.split("\\s+");
            for (String word : words) {
                try {
                    url = new URL(word);
                    log.info("Обнаружена ссылка: " + url);

                    Long senderUid = userInfoService.getUserUidByChatId(chatId);
                    if (senderUid == null) {
                        log.error("Пользователь с chatId {} не найден", chatId);
                        return new SendMessage()
                                .setChatId(chatId)
                                .setText("Пользователь с chatId " + chatId + " не найден на сайте! " +
                                        "Пожалуйста, укажите свой chatId в Личном кабинете.");
                    }

                    User sender = userService.findUserByUid(senderUid);
                    User recipient = userService.findUserByUsername(username);

                    ReferenceDescription referenceDescription = referenceService.addReferenceFromTelegram(url);
                    privateMessageService.sendReferenceToFriendFromTelegram(referenceDescription.getUid(), sender,
                            recipient.getUsername());

                    response = new SendMessage()
                            .setChatId(chatId)
                            .setText("Ссылка отправлена пользователю " + username);
                } catch (MalformedURLException e) {
                    log.debug("Это не ссылка: " + word);
                }
            }
        } else {
            response = getUserNicknameMessage(chatId, messageText);
        }
        return response;
    }

    private SendMessage getUserNicknameMessage(Long chatId, String username) {
        SendMessage response;
        if (userService.checkUserExists(username)) {
            Long senderUid = userInfoService.getUserUidByChatId(chatId);
            User sender = userService.findUserByUid(senderUid);
            User recipient = userService.findUserByUsername(username);

            if ((!friendsService.checkFriendship(sender, recipient)) && (!sender.equals(recipient))) {
                log.error("Пользователь {} не является другом пользователя {}", recipient.getUsername(), sender.getUsername());
                response = new SendMessage()
                        .setChatId(chatId)
                        .setText("Пользователь " + recipient.getUsername() + " отсутствует в вашем списке друзей! " +
                                "Вы можете отправлять ссылки либо себе, либо друзьям.");
            } else {
            recipients.put(chatId, username);
            response = new SendMessage()
                    .setChatId(chatId)
                    .setText("Никнейм адресата установлен: " + username);}
        } else {
            response = new SendMessage()
                    .setChatId(chatId)
                    .setText("Никнейм адресата не указан или не найден на сайте. Пожалуйста, отправьте никнейм!");
        }
        return response;
    }


    @PostConstruct
    public void start() {
        log.info("username: {}, token: {}", botUsername, token);
    }
}
