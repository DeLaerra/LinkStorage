package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.PrivateMessage;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.PrivateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller

public class PrivateMessageController {
    private PrivateMessageService privateMessageService;

    @Autowired
    public PrivateMessageController(PrivateMessageService privateMessageService) {
        this.privateMessageService = privateMessageService;
    }


    @GetMapping("/messages")
    public String messages(@CurrentUser User user, Pageable pageable, Model model) {
        log.info("Получен запрос на отображение страницы сообщений от пользователя с uid - {}", user.getUid());

        List<PrivateMessage> pmInbox = privateMessageService.getInbox(user, pageable);
        for (PrivateMessage privateMessage : pmInbox) {
            privateMessage.getReferenceDescription().setTags();
        }
        pmInbox.sort(Comparator.comparing(PrivateMessage::getSendingTime).reversed());

        List<PrivateMessage> pmSent = privateMessageService.getSent(user, pageable);
        for (PrivateMessage privateMessage : pmSent) {
            privateMessage.getReferenceDescription().setTags();
        }
        pmSent.sort(Comparator.comparing(PrivateMessage::getSendingTime).reversed());

        model.addAttribute("pmInbox", pmInbox);
        model.addAttribute("pmSent", pmSent);

        return "messages";
    }

    @GetMapping("/messages/reject/{pmId}")
    public String cancelMessageAddition(@CurrentUser User user, @PathVariable Long pmId) {
        log.info("Получен запрос на отклонение ссылки из приватного сообщения c pmId- {}", pmId);
        privateMessageService.rejectRequestFromMessage(pmId);

        return "redirect:/messages";
    }

    @GetMapping("/messages/delete/{pmId}")
    public String deleteMessage(@CurrentUser User user, @PathVariable Long pmId) {
        log.info("Получен запрос на удаление приватного сообщения с pmId- {} от пользователя {}", pmId, user.getUsername());
        privateMessageService.deleteMessageByUid(pmId);

        return "redirect:/messages";
    }

    @GetMapping("/messages/accept/{pmId}")
    public String acceptMessageAddition(ReferenceDescription referenceDescription, Model model,
                                        @CurrentUser User user, @PathVariable Long pmId) {
        log.info("Получен запрос на добавление ссылки из приватного сообщения c pmId- {}", pmId);
        privateMessageService.acceptRequestFromMessage(pmId, user, referenceDescription, model);

        return "redirect:/messages";
    }
}
