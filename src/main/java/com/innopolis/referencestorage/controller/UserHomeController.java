package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.Tags;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.service.*;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
public class UserHomeController {
    private ReferenceService referenceService;
    private UserService userService;
    private FriendsService friendsService;
    private FriendshipRequestService friendshipRequestService;

    @Autowired
    public UserHomeController(ReferenceService referenceService, UserService userService, FriendsService friendsService,
                              FriendshipRequestService friendshipRequestService, UserInfoService userInfoService) {
        this.referenceService = referenceService;
        this.userService = userService;
        this.friendsService = friendsService;
        this.friendshipRequestService = friendshipRequestService;
    }

    @GetMapping("/userHome")
    public String showUserReferences(@CurrentUser User user, Model model, Pageable pageable,
                                     HttpServletRequest request,
                                     @RequestParam(name = "sortBy", required = false) String sortBy,
                                     @RequestParam(name = "load", required = false) String load,
                                     @RequestParam(name = "search", required = false) String q,
                                     @RequestParam(name = "area", required = false) String area,
                                     @RequestParam(name = "searchFriends", required = false) String searchFriends) {
        log.info("Получен запрос об отображении ссылок пользователя с uid - {}", user.getUid());
        Page<ReferenceDescription> page = getReferencesPage(user, pageable, sortBy, load);
        model.addAttribute("page", page);
        model.addAttribute("url", "/userHome");
        model.addAttribute("listFriends", friendsService.showAllFriends(user));
        model.addAttribute("searchFriends", searchFriends != null && !"".equals(searchFriends) ?
                userService.findUsers(searchFriends)
                : null);
        Set<String> myTags = getUserTags(page);
        Set<String> mySearchTags = new HashSet<>();
        for (String tag : myTags) {
            mySearchTags.add(tag.replaceFirst("#", ""));
        }
        model.addAttribute("myTags", myTags);
        model.addAttribute("mySearchTags", mySearchTags);
        if (!friendshipRequestService.isEmptyInbox(user)) {
            model.addAttribute("notEmptyInbox", true);
        }
        if (request.getParameter("pmDuplicateError") != null) {
            model.addAttribute("pmDuplicateError", true);
        }
        if (request.getParameter("copyRefError") != null) {
            model.addAttribute("copyRefError", true);
        }

        return "userHome";
    }

    private Page<ReferenceDescription> getReferencesPage(@CurrentUser User user, Pageable pageable,
                                                         @RequestParam(name = "sortBy", required = false) String sortBy,
                                                         @RequestParam(name = "load", required = false) String load) {
        Page<ReferenceDescription> page = referenceService.loadRefsByUserUid(user, pageable);
        page.forEach(ReferenceDescription::setTags); // создание строки для отображения всех тегов
        if (load != null && load.equals("all")) {
            log.info("Получен запрос на отображение всех ссылок пользователя с uid - {}", user.getUid());
            page = referenceService.loadRefsByUserUid(user,
                    PageRequest.of(0, pageable.getPageSize(), Sort.unsorted()));
        } else if (sortBy != null) {
            log.info("Получен запрос на сортировку ссылок пользователя с uid - {}", user.getUid());
            page = getSortedReferences(user, pageable, sortBy);
        }
        return page;
    }

    private Page<ReferenceDescription> getSortedReferences(@CurrentUser User user, Pageable pageable,
                                                           @RequestParam(name = "sortBy", required = false) String sortBy) {
        Page<ReferenceDescription> page;
        switch (sortBy) {
            case "nameDesc":
                log.info("Сортировка ссылок пользователя с uid {} по имени, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").descending()));
                break;
            case "nameAsc":
                log.info("Сортировка ссылок пользователя с uid {} по имени, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending()));
                break;
            case "sourceDesc":
                log.info("Сортировка ссылок пользователя с uid {} по источнику, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("source").descending()));
                break;
            case "sourceAsc":
                log.info("Сортировка ссылок пользователя с uid {} по источнику, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("source").ascending()));
                break;
            case "ratingDesc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("reference.rating").descending()));
                break;
            case "ratingAsc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("reference.rating").ascending()));
                break;
            default:
                log.warn("Неверный аргумент sortBy от пользователя с uid {} при попытке сортировки ссылок", user.getUid());
                throw new IllegalStateException("Неверный аргумент sortBy");
        }
        return page;
    }

    private Set<String> getUserTags(Page<ReferenceDescription> page) {
        Set<String> userTags = new HashSet<>();
        if (!page.isEmpty()) {
            for (ReferenceDescription refDesc : page) {
                Set<Tags> refDescTags = refDesc.getTag();
                if (!refDescTags.isEmpty()) {
                    for (Tags tags : refDescTags) {
                        userTags.add(tags.getName());
                    }
                }
            }
        }
        return userTags;
    }
}
