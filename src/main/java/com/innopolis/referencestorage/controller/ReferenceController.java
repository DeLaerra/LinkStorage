package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.PrivateMessage;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.PrivateMessageService;
import com.innopolis.referencestorage.service.ReferenceService;
import com.innopolis.referencestorage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ReferenceController.
 *
 * @author Roman Khokhlov
 */
@Slf4j
@RequestMapping("/reference")
@Controller
public class ReferenceController {
    private ReferenceService referenceService;
    private PrivateMessageService privateMessageService;
    private UserService userService;

    @Autowired
    public ReferenceController(ReferenceService referenceService, PrivateMessageService privateMessageService, UserService userService) {
        this.referenceService = referenceService;
        this.privateMessageService = privateMessageService;
        this.userService = userService;
    }

    @PostMapping("/add/{userId}")
    public String addReference(ReferenceDescription reference, Model model, BindingResult mapping1BindingResult,
                               RedirectAttributes redirectAttributes,
                               @PathVariable Long userId,
                               @RequestParam(name = "url", required = false) String url) {
        log.info("Получен запрос на добавление новой записи ссылки: \n userId - {}, \n reference - {} ", userId, url);
        referenceService.addReference(userId, reference, url, model);

        if (model.getAttribute("copyRefError") != null) {
            redirectAttributes.addAttribute("copyRefError", true);
        }

        return "redirect:/userHome";
    }

    @PostMapping("/copy/{refId}")
    public String addReferenceToUserHome(ReferenceDescription reference, Model model, BindingResult mapping1BindingResult,
                                         RedirectAttributes redirectAttributes,
                                         @CurrentUser User user,
                                         @PathVariable Long refId) {
        log.info("Получен запрос на копирование записи ссылки в userHome: \n userId - {}, \n refDescriptionUid - {} ", user.getUid(), refId);
        referenceService.copyReference(refId, user, reference, model);

        if (model.getAttribute("copyRefError") != null) {
            redirectAttributes.addAttribute("copyRefError", true);
        }

        return "redirect:/userHome";
    }

    @PostMapping("/update/{refId}")
    public String updateReference(ReferenceDescription reference, Model model,
                                  @PathVariable Long refId,
                                  @RequestParam(name = "url", required = false) String url) {
        log.info("Получен запрос на обновление записи ссылки: \n refId - {}, \n reference - {} ", refId, url);
        referenceService.updateReference(refId, reference, url);
        return "redirect:/userHome";
    }

    @GetMapping("delete/{refId}")
    public String deleteReference(@PathVariable Long refId, Model model) {
        log.info("Получен запрос на удаление элемента: \n refId - {}", refId);
        ReferenceDescription refDelete = referenceService.deleteReference(refId);
        model.addAttribute("referenceDelete", refDelete);
        return "redirect:/userHome";
    }

    @PostMapping("/send/{refId}")
    public String sendReferenceToFriend(PrivateMessage privateMessage, Model model,
                                        BindingResult mapping1BindingResult,
                                        RedirectAttributes redirectAttributes,
                                        @CurrentUser User user,
                                        @PathVariable Long refId,
                                        @RequestParam(name = "receiver", required = false) String friendUsername,
                                        @RequestParam(name = "text", required = false) String text) {
        log.info("Получен запрос на отправку ссылки: \n username - {}, \n refDescriptionUid - {}, \n friendUsername - {} ",
                user.getUsername(), refId, friendUsername);

        model.addAttribute("userFriends", userService.loadUserByUsername(user.getUsername()));
            privateMessageService.sendReferenceToFriend(privateMessage, refId, user, friendUsername, text, model);

        if (model.getAttribute("pmDuplicateError") != null) {
            redirectAttributes.addAttribute("pmDuplicateError", true);
            return "redirect:/userHome";
        }

        log.info("Отправлена ссылка: \n username - {}, \n refDescriptionUid - {}, \n friendUsername - {} ",
                user.getUsername(), refId, friendUsername);
        return "redirect:/userHome";
    }

    @PostMapping("/send-new")
    public String sendNewReferenceToFriend(PrivateMessage privateMessage, ReferenceDescription referenceDescription, Model model,
                                           BindingResult mapping1BindingResult,
                                           RedirectAttributes redirectAttributes,
                                           @CurrentUser User user,
                                           @RequestParam(name = "url", required = false) String url,
                                           @RequestParam(name = "receiver", required = false) String friendUsername,
                                           @RequestParam(name = "text", required = false) String text) {
        log.info("Получен запрос на отправку новой ссылки: \n username - {}, \n friendUsername - {} ",
                user.getUsername(), friendUsername);
        model.addAttribute("userFriends", userService.loadUserByUsername(user.getUsername()));

        referenceService.addReference(0L, referenceDescription, url, model);
        privateMessageService.sendReferenceToFriend(privateMessage, referenceDescription.getUid(), user, friendUsername, text, model);

        if (model.getAttribute("pmDuplicateError") != null) {
            redirectAttributes.addAttribute("pmDuplicateError", true);
            return "redirect:/userHome";
        }

        log.info("Отправлена новая ссылка: \n username - {}, \n friendUsername - {} ",
                user.getUsername(), friendUsername);
        return "redirect:/userHome";
    }
}