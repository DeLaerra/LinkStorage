package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class MyAccountController {
    private UserInfoService userInfoService;

    @Autowired
    public MyAccountController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }


    @GetMapping("/myAccount")
    public String myAccount(@CurrentUser User user, Model model) {
        log.info("Получен запрос на отображение личного кабинета от пользователя с uid - {}", user.getUid());
        UserInfo userInfo = userInfoService.getUserInfoWithUserUID(user);
        model.addAttribute("userInfo", userInfo);
        model.addAttribute("birthDateStr", userInfo.getBirthDate());
        model.addAttribute("avatarImage", userInfoService.getFileFromUserInfo(userInfo.getAvatar()));
        return "myAccount";
    }

    @PostMapping("/myAccount")
    public String editUserDetails(@CurrentUser User user, UserInfo userInfo, String birthDateStr, MultipartFile file, Model model) {
        log.info("Получен запрос на изменение данных в личном кабинете от пользователя с uid {}: \n userInfo - {}, \n birthDate - {} ", user.getUid(), userInfo, birthDateStr);
        userInfoService.checkAndAddData(user, userInfo, birthDateStr, file, model);
        userInfoService.saveUserInfo(userInfo);
        if (model.getAttribute("userInfoNameError") != null || model.getAttribute("userInfoSurnameError") != null ||
                model.getAttribute("userInfoAgeError") != null || model.getAttribute("userInfoBirthDateError") != null ||
                model.getAttribute("userInfoAvatarError") != null) {
            UserInfo originalUserInfo = userInfoService.getUserInfoWithUserUID(user);
            model.addAttribute("userInfo", originalUserInfo);
            model.addAttribute("birthDateStr", originalUserInfo.getBirthDate());
            model.addAttribute("avatarImage", userInfoService.getFileFromUserInfo(originalUserInfo.getAvatar()));
            return "myAccount";
        }
        return "redirect:/myAccount";
    }
}