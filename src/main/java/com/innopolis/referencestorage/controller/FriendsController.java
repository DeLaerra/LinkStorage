package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.FriendshipRequest;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.FriendshipRequestRepo;
import com.innopolis.referencestorage.repos.ReferenceDescriptionRepo;
import com.innopolis.referencestorage.repos.UserRepo;
import com.innopolis.referencestorage.service.FriendsService;
import com.innopolis.referencestorage.service.ReferenceService;
import com.innopolis.referencestorage.service.ReferenceSortingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SessionAttributes({"sortByText"})
@Controller
public class FriendsController {

    private User userFriend;
    private ReferenceService referenceService;
    private FriendsService friendsService;
    private FriendshipRequestRepo friendshipRequestRepo;
    private ReferenceDescriptionRepo referenceDescriptionRepo;
    private UserRepo userRepo;
    private ReferenceSortingService referenceSortingService;


    @Autowired
    public FriendsController(FriendsService friendsService, ReferenceService referenceService, FriendshipRequestRepo friendshipRequestRepo,
                             ReferenceDescriptionRepo referenceDescriptionRepo, UserRepo userRepo,
                             ReferenceSortingService referenceSortingService) {
        this.friendsService = friendsService;
        this.referenceService = referenceService;
        this.friendshipRequestRepo = friendshipRequestRepo;
        this.referenceDescriptionRepo = referenceDescriptionRepo;
        this.userRepo = userRepo;
        this.referenceSortingService = referenceSortingService;
    }

    @ModelAttribute("sortByText")
    public String populateSortByText() {
        return "";
    }

    @GetMapping("/addFriendsReference/{friendUid}/{refDescrId}")
    public String addFriendsReference(
            @CurrentUser User user, Model model,
            ReferenceDescription referenceDescription,
            RedirectAttributes redirectAttributes,
            BindingResult mapping1BindingResult,
            @PathVariable Long friendUid,
            @PathVariable Long refDescrId) {

        referenceService.copyReference(refDescrId, user, referenceDescription, model);
        Reference reference = referenceDescriptionRepo.findByUid(refDescrId).getReference();

        return "redirect:/friend/{friendUid}";
    }

        @GetMapping("/deleteFriend/{friendUid}")
    public String deleteFriend(@CurrentUser User user, Model model,
                               @PathVariable Long friendUid) {

        friendsService.deleteFriends(user, userRepo.findByUid(friendUid));
        if (friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(user,
                userRepo.findByUid(friendUid), 1)) {
        ArrayList<FriendshipRequest> requests1 = friendshipRequestRepo.findBySenderAndRecipientAndAcceptionStatusEquals(user,
                userRepo.findByUid(friendUid), 1);
        FriendshipRequest request = requests1.get(requests1.size()-1);
        Long reqId = request.getUid();
        friendshipRequestRepo.deleteById(reqId);

        }

        return "redirect:/friend/{friendUid}";
    }


    @GetMapping("/friend/{friendUid}")
    public String showUserReferences(
            @CurrentUser User user, Model model, Pageable pageable, ReferenceDescription referenceDescription,
            @PathVariable Long friendUid,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "load", required = false) String load,
            @RequestParam(name = "search", required = false) String q,
            @RequestParam(name = "area", required = false) String area) {

        userFriend = friendsService.findUserByUid(friendUid);
        List<FriendshipRequest> frreq = friendshipRequestRepo.findByRecipient(userFriend);
        for (FriendshipRequest fr : frreq) {
            if (fr.getSender().getUid().equals(user.getUid()) && fr.getAcceptionStatus() != 2) {
                model.addAttribute("requestSent", "Заявка отправлена");
            }
        }
        if (friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(friendUid),
                user, 1) || friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(friendUid),
                user, 0) || friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(friendUid),
                user, 3)) {
            model.addAttribute("requestAcceptionAllow", "Принять заявку");
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

        Page<ReferenceDescription> page = referenceSortingService.getSortedReferences(userFriend, pageable, (String) model.getAttribute("sortByText"), load);
        page.forEach(ReferenceDescription::setTags); // создание строки для отображения всех тегов

        List<ReferenceDescription> friendReferences = referenceDescriptionRepo.findByUidUser(friendUid);
        for (ReferenceDescription rd : friendReferences) {
            if (referenceDescriptionRepo.existsByReferenceUidAndUidUser(rd.getReference().getUid(), user.getUid())) {
                rd.setIsExistAtFriend(1);
            } else rd.setIsExistAtFriend(0);
        }

        model.addAttribute("deleteMessage", "Удалить из друзей");
        model.addAttribute("page", page);
        model.addAttribute("url", "/friend");
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getUid());
        model.addAttribute("url", "/friend/" + friendUid);


        return "friend";
    }
}

