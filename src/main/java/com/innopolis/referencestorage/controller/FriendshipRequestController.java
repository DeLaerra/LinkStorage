package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.FriendshipRequest;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.FriendshipRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

/**
 * FriendshipRequestController.
 *
 * @author Roman Khokhlov
 */

@Slf4j
@Controller
public class FriendshipRequestController {
    private FriendshipRequestService friendshipRequestService;

    @Autowired
    public FriendshipRequestController(FriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
    }

    @GetMapping("/sendRequest/{recipientId}")
    public String sendReferenceToFriend(FriendshipRequest friendshipRequest, Model model,
                                        BindingResult mapping1BindingResult,
                                        RedirectAttributes redirectAttributes,
                                        @CurrentUser User user,
                                        @PathVariable Long recipientId,
                                        @RequestParam(name = "receiver", required = false) String friendUsername,
                                        @RequestParam(name = "text", required = false) String text) {

        friendshipRequestService.sendFriendshipRequestToUser(friendshipRequest, user, recipientId, text, model);
        model.addAttribute("requestSent", "Заявка отправлена");
        if (model.getAttribute("pmDuplicateError") != null) {
            redirectAttributes.addAttribute("pmDuplicateError", true);
            return "redirect:/friend/{recipientId}";
        }
        log.info("Отправлен запрос на добавление в друзья: \n username - {}, \n friendUsername - {} ",
                user.getUsername(), friendUsername);
        return "redirect:/friend/{recipientId}";
    }

    @GetMapping("/requests")
    public String messages(@CurrentUser User user, Pageable pageable, Model model) {
        log.info("Получен запрос на отображение страницы заявок от пользователя с uid - {}", user.getUid());

        List<FriendshipRequest> frInbox = friendshipRequestService.getInbox(user, pageable);
        frInbox.sort(Comparator.comparing(FriendshipRequest::getSendingTime).reversed());

        List<FriendshipRequest> frSent = friendshipRequestService.getSent(user, pageable);
        frSent.sort(Comparator.comparing(FriendshipRequest::getSendingTime).reversed());

        model.addAttribute("frInbox", frInbox);
        model.addAttribute("frSent", frSent);

        return "requests";
    }

    @GetMapping("/requests/reject/{frId}")
    public String cancelMessageAddition(@CurrentUser User user, @PathVariable Long frId) {
        log.info("Получен запрос на отклонение заявки  c frId- {}", frId);
        friendshipRequestService.rejectRequestFromMessage(frId);
        return "redirect:/requests";
    }

    @GetMapping("/requests/delete/{frId}")
    public String deleteMessage(@CurrentUser User user, @PathVariable Long frId) {
        log.info("Получен запрос на удаление сообщения с frId- {} от пользователя {}", frId, user.getUsername());
        friendshipRequestService.deleteMessageByUid(frId);
        return "redirect:/requests";
    }

    @GetMapping("/requests/accept/{frId}")
    public String acceptMessageAddition(Model model,
                                        @CurrentUser User user, @PathVariable Long frId) {
        log.info("Получен запрос на добавление в друзья из сообщения c frId- {}", frId);
        friendshipRequestService.acceptRequestFromMessage(frId, user, model);
        return "redirect:/requests";
    }
}

