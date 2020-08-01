package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.FriendsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class FriendsController {

    private FriendsService friendsService;

    public FriendsController(FriendsService friendsService) {
        this.friendsService = friendsService;
    }

    @GetMapping("/friend")
    public String getFriend(@CurrentUser User user, Model model,
                            @RequestParam(name = "idFriend", required = false) String idFriend) {
        model.addAttribute("friend", friendsService.findUserByUid(Long.parseLong(idFriend)));
        return "friend";
    }

    @GetMapping("/addFriend")
    public String addFriend(@CurrentUser User user, Model model,
                            @RequestParam(name = "addFriend", required = false) String addFriend) {
        friendsService.addFriends(user.getUid(), Long.parseLong(addFriend));
        return "redirect:/userHome";
    }
}
