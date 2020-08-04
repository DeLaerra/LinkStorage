//package com.innopolis.referencestorage.util.TelegramBot;
//
//import com.innopolis.referencestorage.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.ApiContextInitializer;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.MessageEntity;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import javax.annotation.PostConstruct;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//
//@Slf4j
//@Component
//public class LinkStorageBot extends TelegramLongPollingBot {
//    static {
//        ApiContextInitializer.init();
//    }
//
//    private UserService userService;
//    private Map<Long, String> users = new HashMap<>();
//
//
//    @Value("1382682447:AAF5lYS10l43ZNgrXuEwm2qCnArQpEfAy_M")
//    private String token;
//
//    @Value("link_storage_bot")
//    private String botUsername;
//
//
//    @Autowired
//    public LinkStorageBot(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public String getBotToken() {
//        return token;
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        log.debug("New update received. UpdateID: " + update.getUpdateId());
//        SendMessage response = null;
//        SendMessage badResponse = getBadResponseMessage(update.getMessage().getChatId());
//
//        if (update.hasMessage()) {
//            Message message = update.getMessage();
//            log.info("Received message \"{}\" from {}", update.getMessage().getText(), update.getMessage().getChatId());
//            response = parseCommands(message, badResponse);
//        } else {
//            response = badResponse;
//        }
//
//        try {
//            execute(response);
//            log.info("Sent message \"{}\" to {}", response.getText(), response.getChatId());
//        } catch (TelegramApiException e) {
//            log.error("Failed to send message \"{}\" to {} due to error: {}",
//                    response.getText(), response.getChatId(), e.getMessage());
//        }
//    }
//
//    private SendMessage getBadResponseMessage(Long chatId) {
//        return new SendMessage()
//                .setChatId(chatId)
//                .setText("Это не ссылка!");
//    }
//
//
//    private SendMessage parseCommands(Message message, SendMessage badResponse) {
//        SendMessage response = null;
//        Long chatId = message.getChatId();
//        String messageText = message.getText();
//        String username = users.get(chatId);
//
//        if (message.hasText()) {
//            if (messageText.equals("/start")) {
//                response = new SendMessage()
//                        .setChatId(chatId)
//                        .setText("Здравствуйте! Пожалуйста, укажите никнейм пользователя, которому вы хотите отправлять ссылки");
//            } else if (messageText.startsWith("/to")) {
//                String nickname = messageText.replace("/to ", "");
//                response = getUserNicknameMessage(chatId, nickname);
//            } else if (messageText.startsWith("/help")) {
//                response = new SendMessage()
//                        .setChatId(chatId)
//                        .setText("Перед отправкой ссылок необходимо указать адресата: отправьте сообщение с нужным ником." + "\n" +
//                                "Для смены адресата отправьте команду /to и никнейм нового адресата. Например, /to admin");
//            } else {
//                response = getURLFromMessage(message, badResponse, username);
//            }
//
//        } else {
//            response = badResponse;
//            if (message.getCaptionEntities() != null) {
//                for (MessageEntity messageEntity : message.getCaptionEntities()) {
//                    log.info(messageEntity.toString());
//                    try {
//                        URL url = new URL(messageEntity.getText());
//                        log.info("Обнаружена ссылка во вложении" + url);
//
//                        response = new SendMessage()
//                                .setChatId(message.getChatId())
//                                .setText("Ссылка из вложения отправлена пользователю " + username);
//                    } catch (MalformedURLException ex) {
//                        log.warn("Во вложении нет ссылки!");
//                    }
//                }
//            }
//        }
//
//        return response;
//    }
//
//
//    private SendMessage getURLFromMessage(Message message, SendMessage badResponse, String username) {
//        URL url;
//        SendMessage response;
//        Long chatId = message.getChatId();
//        String messageText = message.getText();
//
//        if (username != null) {
//            response = badResponse;
//
//            // separate input by spaces (URLs don't have spaces)
//            String[] words = messageText.split("\\s+");
//            for (String word : words) {
//                try {
//                    url = new URL(word);
//                    log.info("Обнаружена ссылка: " + url);
//
//                    response = new SendMessage()
//                            .setChatId(chatId)
//                            .setText("Ссылка отправлена пользователю " + username);
//                } catch (MalformedURLException e) {
//                    log.debug("Это не ссылка: " + word);
//                }
//            }
//        } else {
//            response = getUserNicknameMessage(chatId, messageText);
//        }
//        return response;
//    }
//
//    private SendMessage getUserNicknameMessage(Long chatId, String messageText) {
//        SendMessage response;
//        if (userService.checkUserExists(messageText)) {
//            users.put(chatId, messageText);
//            response = new SendMessage()
//                    .setChatId(chatId)
//                    .setText("Никнейм адресата установлен: " + messageText);
//        } else {
//            response = new SendMessage()
//                    .setChatId(chatId)
//                    .setText("Никнейм адресата не указан. Пожалуйста, отправьте никнейм!");
//        }
//        return response;
//    }
//
//
//    @PostConstruct
//    public void start() {
//        log.info("username: {}, token: {}", botUsername, token);
//    }
//}
