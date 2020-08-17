package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.FriendshipRequest;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.FriendshipRequestRepo;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@SessionAttributes({"sortByText"})
@Controller
public class FriendsController {

    private User userFriend;
    private ReferenceService referenceService;
    private FriendsService friendsService;
    private FriendshipRequestRepo friendshipRequestRepo;

    @Autowired
    public FriendsController(FriendsService friendsService, ReferenceService referenceService, FriendshipRequestRepo friendshipRequestRepo) {
        this.friendsService = friendsService;
        this.referenceService = referenceService;
        this.friendshipRequestRepo = friendshipRequestRepo;
    }

    @ModelAttribute("sortByText")
    public String populateSortByText() {
        return "";
    }

    @GetMapping("/addFriend")
    public String addFriend(@CurrentUser User user, Model model,
                            @RequestParam(name = "addFriend", required = false) String addFriend) {
        model.addAttribute("userfriend", friendsService.findUserByUid(Long.parseLong(addFriend)));
        friendsService.addFriends(user.getUid(), Long.parseLong(addFriend));
        model.addAttribute("addedFriend", "Пользователь добавлен в друзья");
        return "redirect:/userHome";
    }

    @GetMapping("/friend/{friendUid}")
    public String showUserReferences(@CurrentUser User user, Model model, Pageable pageable,
                                     @PathVariable Long friendUid,
                                     @RequestParam(name = "sortBy", required = false) String sortBy,
                                     @RequestParam(name = "load", required = false) String load,
                                     @RequestParam(name = "idFriend", required = false) String idFriend) {

        userFriend = friendsService.findUserByUid(friendUid);
        List<FriendshipRequest> frreq = friendshipRequestRepo.findByRecipient(userFriend);
        for (FriendshipRequest fr : frreq) {
            if (fr.getSender().getUid().equals(user.getUid()) && fr.getAcceptionStatus() != 2) {
                model.addAttribute("requestSent", "Заявка отправлена");
            }
        }

        if (sortBy != null && !sortBy.equals("")) {
            model.addAttribute("sortByText", sortBy);
        }

        if (!(friendsService.checkFriendship(user, userFriend))) {
            model.addAttribute("notAddedFriend", "Пользователь у Вас не в друзьях");
        }
        model.addAttribute("friend", friendsService.findUserByUid(friendUid));
        model.addAttribute("friendName", userFriend.getUsername());
        log.info("Получен запрос об отображении ссылок пользователя с uid - {}", userFriend);
        Page<ReferenceDescription> page = getReferencesPage(userFriend, pageable, (String) model.getAttribute("sortByText"), load);
        page.forEach(ReferenceDescription::setTags); // создание строки для отображения всех тегов
        model.addAttribute("page", page);
        model.addAttribute("url", "/friend/" + friendUid);


        return "friend";
    }

    private Page<ReferenceDescription> getReferencesPage(@CurrentUser User user, Pageable pageable, String sortBy,
                                                         String load) {
        return getSortedReferences(userFriend, pageable, sortBy);
    }

    private Page<ReferenceDescription> getSortedReferences(@CurrentUser User user, Pageable pageable, String sortBy) {
        Page<ReferenceDescription> page;

        if (sortBy == null) sortBy = "default";
        switch (sortBy) {
            case "default":
                log.info("Сортировка ссылок пользователя с uid {} по дате, по-убыванию", userFriend.getUid());
                page = referenceService.loadRefsByUserUid(userFriend,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("additionDate").descending()));
                break;
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

