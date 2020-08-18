package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.Tags;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@SessionAttributes({"sortByText"})
@Controller
public class UserHomeController {
    private ReferenceService referenceService;
    private UserService userService;
    private FriendsService friendsService;
    private FriendshipRequestService friendshipRequestService;
    private PrivateMessageService privateMessageService;
    private ReferenceSortingService referenceSortingService;

    @Autowired
    public UserHomeController(ReferenceService referenceService, UserService userService, FriendsService friendsService,
                              FriendshipRequestService friendshipRequestService, PrivateMessageService privateMessageService,
                              ReferenceSortingService referenceSortingService) {
        this.referenceService = referenceService;
        this.userService = userService;
        this.friendsService = friendsService;
        this.friendshipRequestService = friendshipRequestService;
        this.privateMessageService = privateMessageService;
        this.referenceSortingService = referenceSortingService;
    }

    @ModelAttribute("sortByText")
    public String populateSortByText() {
        return "";
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
        if (sortBy != null && !sortBy.equals("")) {
            model.addAttribute("sortByText", sortBy);
        }

        Page<ReferenceDescription> page = getReferencesPage(user, pageable, (String) model.getAttribute("sortByText"), load);

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
        if (privateMessageService.isNotEmptyPMInbox(user)) {
            model.addAttribute("notEmptyPMInbox", true);
        }

        if (request.getParameter("pmDuplicateError") != null) {
            model.addAttribute("pmDuplicateError", true);
        }
        if (request.getParameter("copyRefError") != null) {
            model.addAttribute("copyRefError", true);
        }

        return "userHome";
    }

    private Page<ReferenceDescription> getReferencesPage(@CurrentUser User user, Pageable pageable, String sortBy,
                                                         @RequestParam(name = "load", required = false) String load) {
        Page<ReferenceDescription> page = referenceService.loadRefsByUserUid(user, pageable);
        page.forEach(ReferenceDescription::setTags); // создание строки для отображения всех тегов
        return referenceSortingService.getSortedReferences(user, pageable, sortBy, load);
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
