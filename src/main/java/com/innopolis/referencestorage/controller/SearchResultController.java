package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.FriendsService;
import com.innopolis.referencestorage.service.ReferenceSearchService;
import com.innopolis.referencestorage.service.ReferenceSortingService;
import com.innopolis.referencestorage.service.UserService;
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

import java.util.List;

@Slf4j
@SessionAttributes({"searchText", "areaText", "sortByText"})
@Controller
public class SearchResultController {
    private ReferenceSearchService referenceSearchService;
    private UserService userService;
    private FriendsService friendsService;
    private ReferenceSortingService referenceSortingService;

    @Autowired
    public SearchResultController(ReferenceSearchService referenceSearchService, UserService userService,
                                  FriendsService friendsService, ReferenceSortingService referenceSortingService) {
        this.referenceSearchService = referenceSearchService;
        this.userService = userService;
        this.friendsService = friendsService;
        this.referenceSortingService = referenceSortingService;
    }

    @ModelAttribute("searchText")
    public String populateSearchText() {
        return "";
    }

    @ModelAttribute("areaText")
    public String populateAreaText() {
        return "";
    }

    @ModelAttribute("sortByText")
    public String populateSortByText() {
        return "";
    }

    @GetMapping("/searchResult")
    public String showRefsPage(@CurrentUser User user, Model model, Pageable pageable,
                               @RequestParam(name = "sortBy", required = false) String sortBy,
                               @RequestParam(name = "load", required = false) String load,
                               @RequestParam(name = "search", required = false) String q,
                               @RequestParam(name = "area", required = false) String area,
                               @RequestParam(name = "searchFriends", required = false) String searchFriends) {
        Page<ReferenceDescription> page;
        if (q != null && !q.equals("")) {
            model.addAttribute("searchText", q);
        }

        if (area != null && !area.equals("")) {
            model.addAttribute("areaText", area);
        }

        if (sortBy != null && !sortBy.equals("")) {
            model.addAttribute("sortByText", sortBy);
        }

        log.info("Получен поисковый запрос от пользователя с uid {} с текстом {}", user.getUid(), q);
        page = getSearchResultReferencesPage(user, pageable, (String) model.getAttribute("sortByText"), load,
                (String) model.getAttribute("searchText"),
                (String) model.getAttribute("areaText"));
        page.forEach(ReferenceDescription::setTags);
        model.addAttribute("page", page);
        model.addAttribute("url", "/searchResult");
        model.addAttribute("listFriends", friendsService.showAllFriends(user));
        model.addAttribute("searchFriends", searchFriends != null && !"".equals(searchFriends) ?
                userService.findUsers(searchFriends)
                : null);
        return "searchResult";
    }

    private Page<ReferenceDescription> getSearchResultReferencesPage(User user, Pageable pageable, String sortBy,
                                                                     String load, String q, String area) {
        List<ReferenceDescription> references;

        if (q.startsWith("#")) {
            if (area != null && area.equals("all")) {
                references = referenceSearchService.fullTagSearchPublicReferencesOnly(q, user);
                log.info("Выполнен поиск по всем публичным ссылкам сайта по запросу пользователя с uid {} с текстом {}", user.getUid(), q);
            } else {
                references = referenceSearchService.fullTagSearchReferencesByUserUid(q, user);
                log.info("Выполнен поиск по личным ссылкам пользователя с uid {} с текстом {}", user.getUid(), q);
            }
        } else {
            if (area != null && area.equals("all")) {
                references = referenceSearchService.fullTextSearchPublicReferencesOnly(q, user);
                log.info("Выполнен поиск по всем публичным ссылкам сайта по запросу пользователя с uid {} с текстом {}", user.getUid(), q);
            } else {
                references = referenceSearchService.fullTextSearchReferencesByUserUid(q, user);
                log.info("Выполнен поиск по личным ссылкам пользователя с uid {} с текстом {}", user.getUid(), q);
            }
        }
        return referenceSortingService.getSortedSearchResultReferences(user, pageable, references, sortBy, load);
    }
}
