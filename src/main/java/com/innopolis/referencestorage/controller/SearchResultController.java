package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.FriendsService;
import com.innopolis.referencestorage.service.ReferenceSearchService;
import com.innopolis.referencestorage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Comparator;
import java.util.List;

@Slf4j
@SessionAttributes({"searchText", "areaText", "sortByText"})
@Controller
public class SearchResultController {
    private ReferenceSearchService referenceSearchService;
    private UserService userService;
    private FriendsService friendsService;

    @Autowired
    public SearchResultController(ReferenceSearchService referenceSearchService, UserService userService,
                                  FriendsService friendsService) {
        this.referenceSearchService = referenceSearchService;
        this.userService = userService;
        this.friendsService = friendsService;
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
        page = getSearchResultReferencesPage(user, model, pageable, (String) model.getAttribute("sortByText"), load,
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

    private Page<ReferenceDescription> getSearchResultReferencesPage(@CurrentUser User user, Model model, Pageable pageable,
                                                                     String sortBy,
                                                                     String load,
                                                                     String q,
                                                                     String area) {
        List<ReferenceDescription> references;
        Page<ReferenceDescription> page;

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

            page = getSortedReferences(user, pageable, references, sortBy);

        return page;
    }

    private Page<ReferenceDescription> getSortedReferences(@CurrentUser User user, Pageable pageable,
                                                           List<ReferenceDescription> references, String sortBy) {
        Page<ReferenceDescription> page = null;
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        int total = references.size();
        int start = Math.toIntExact(pageRequest.getOffset());
        int end = (start + pageRequest.getPageSize()) > references.size() ? references.size() : (start + pageRequest.getPageSize());

        if (sortBy == null || sortBy.equals("")) sortBy = "default";
        switch (sortBy) {
            case "default":
                log.info("Сортировка результатов поиска пользователя с uid {} по дате, по-убыванию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getAdditionDate).reversed());
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            case "nameDesc":
                log.info("Сортировка результатов поиска пользователя с uid {} по имени, по-убыванию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getName).reversed());
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            case "nameAsc":
                log.info("Сортировка результатов поиска пользователя с uid {} по имени, по-возрастанию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getName));
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            case "sourceDesc":
                log.info("Сортировка результатов поиска пользователя с uid {} по источнику, по-убыванию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getSource).reversed());
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            case "sourceAsc":
                log.info("Сортировка результатов поиска пользователя с uid {} по источнику, по-возрастанию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getSource));
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            case "ratingDesc":
                log.info("Сортировка результатов поиска пользователя с uid {} по рейтингу, по-убыванию", user.getUid());
                references.sort(Comparator
                        .comparing((ReferenceDescription referenceDescription) -> referenceDescription.getReference().getRating())
                        .reversed());
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            case "ratingAsc":
                log.info("Сортировка результатов поиска пользователя с uid {} по рейтингу, по-возрастанию", user.getUid());
                references.sort(Comparator
                        .comparing((ReferenceDescription referenceDescription) -> referenceDescription.getReference().getRating()));
                page = new PageImpl<>(references.subList(start, end), pageRequest, total);
                break;
            default:
                log.warn("Неверный аргумент sortBy от пользователя с uid {} при попытке сортировки результатов поиска", user.getUid());
                throw new IllegalStateException("Неверный аргумент sortBy");
        }
        return page;
    }
}
