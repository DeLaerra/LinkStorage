package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.FriendsService;
import com.innopolis.referencestorage.service.ReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class FriendsController {

    private User userFriend;
    private ReferenceService referenceService;
    private FriendsService friendsService;

    @Autowired
    public FriendsController(FriendsService friendsService, ReferenceService referenceService) {
        this.friendsService = friendsService;
        this.referenceService = referenceService;
    }

    @GetMapping("/addFriend")
    public String addFriend(@CurrentUser User user, Model model,
                            @RequestParam(name = "addFriend", required = false) String addFriend) {
        model.addAttribute("userfriend", friendsService.findUserByUid(Long.parseLong(addFriend)));
        friendsService.addFriends(user.getUid(), Long.parseLong(addFriend));
        model.addAttribute("addedFriend", "Пользователь добавлен в друзья");
        return "redirect:/userHome";
    }

    @GetMapping("/friend")
    public String showUserReferences(@CurrentUser User user, Model model, Pageable pageable,
                                     @RequestParam(name = "sortBy", required = false) String sortBy,
                                     @RequestParam(name = "load", required = false) String load,
                                     @RequestParam(name = "search", required = false) String q,
                                     @RequestParam(name = "area", required = false) String area,
                                     @RequestParam(name = "idFriend", required = false) String idFriend) {
        userFriend = friendsService.findUserByUid(Long.parseLong(idFriend));

        if (!(friendsService.checkFriendship(user, userFriend))) {
            model.addAttribute("notAddedFriend", "Пользователь у Вас не в друзьях");
        }
        model.addAttribute("friendName", userFriend.getUsername());
        log.info("Получен запрос об отображении ссылок пользователя с uid - {}", userFriend);
        Page<ReferenceDescription> page = getReferencesPage(userFriend, pageable, sortBy, load);
        model.addAttribute("page", page);
        model.addAttribute("url", "/friend");


        return "friend";
    }

    private Page<ReferenceDescription> getReferencesPage(@CurrentUser User user, Pageable pageable,
                                                         @RequestParam(name = "sortBy", required = false) String sortBy,
                                                         @RequestParam(name = "load", required = false) String load) {
        Page<ReferenceDescription> page = referenceService.loadRefsByUserUid(userFriend, pageable);

        if (load != null && load.equals("all")) {
            log.info("Получен запрос на отображение всех ссылок пользователя с uid - {}", userFriend.getUid());
            page = referenceService.loadRefsByUserUid(userFriend,
                    PageRequest.of(0, pageable.getPageSize(), Sort.unsorted()));
        } else if (sortBy != null) {
            log.info("Получен запрос на сортировку ссылок пользователя с uid - {}", userFriend.getUid());
            page = getSortedReferences(userFriend, pageable, sortBy);
        }
        return page;
    }

    private Page<ReferenceDescription> getSortedReferences(@CurrentUser User user, Pageable pageable,
                                                           @RequestParam(name = "sortBy", required = false) String sortBy) {
        Page<ReferenceDescription> page;
        switch (sortBy) {
            case "nameDesc":
                log.info("Сортировка ссылок пользователя с uid {} по имени, по-убыванию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").descending()));
                break;
            case "nameAsc":
                log.info("Сортировка ссылок пользователя с uid {} по имени, по-возрастанию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending()));
                break;
            case "sourceDesc":
                log.info("Сортировка ссылок пользователя с uid {} по источнику, по-убыванию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("source").descending()));
                break;
            case "sourceAsc":
                log.info("Сортировка ссылок пользователя с uid {} по источнику, по-возрастанию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("source").ascending()));
                break;
            case "ratingDesc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-убыванию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("reference.rating").descending()));
                break;
            case "ratingAsc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-возрастанию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("reference.rating").ascending()));
                break;
            default:
                log.warn("Неверный аргумент sortBy от пользователя с uid {} при попытке сортировки ссылок", userFriend.getUid());
                throw new IllegalStateException("Неверный аргумент sortBy");
        }
        return page;
    }

}

