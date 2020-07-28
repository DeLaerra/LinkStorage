package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.commons.utils.EmailValidator;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.UserInfoService;
import com.innopolis.referencestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * RegistrationController.
 *
 * @author Roman Khokhlov
 */
@Controller
public class RegistrationController {
    private UserService userService;
    private UserInfoService userInfoService;

    @Autowired
    public RegistrationController(UserService userService, UserInfoService userInfoService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
    }


    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        if (!user.getPassword().equals(user.getPasswordConfirmation())){
            model.put("passwordError", "Введённые пароли не совпадают!");
            return "registration";
        }
        if (!EmailValidator.isValid(user.getEmail())) {
            model.put("emailError", "Некорректный email");
            return "registration";
        }
        if (!userService.saveUser(user)) {
            model.put("usernameError", "Пользователь с таким никнеймом или email уже существует!");
            return "registration";
        }
        userInfoService.createUserDetails(user.getUid());
        return "redirect:/login";
    }
}