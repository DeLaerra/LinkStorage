package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.ReferenceSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
@SessionAttributes({"searchText", "areaText"})
@Controller
public class SearchResultController {
    private ReferenceSearchService referenceSearchService;

    @Autowired
    public SearchResultController(ReferenceSearchService referenceSearchService) {
        this.referenceSearchService = referenceSearchService;
    }

    @ModelAttribute("searchText")
    public String populateSearchText() {
        return "";
    }

    @ModelAttribute("areaText")
    public String populateAreaText() {
        return "";
    }

    @GetMapping("/searchResult")
    public String showRefsPage(@CurrentUser User user, Model model, Pageable pageable,
                               @RequestParam(name = "sortBy", required = false) String sortBy,
                               @RequestParam(name = "load", required = false) String load,
                               @RequestParam(name = "search", required = false) String q,
                               @RequestParam(name = "area", required = false) String area) {
        Page<ReferenceDescription> page;
        if (q != null && !q.equals("")) {
            model.addAttribute("searchText", q);
        }

        if (area != null && !area.equals("")) {
            model.addAttribute("areaText", area);
        }
        log.info("Получен поисковый запрос от пользователя с uid {} с текстом {}", user.getUid(), q);
        page = getSearchResultReferencesPage(user, model, pageable, sortBy, load,
                (String) model.getAttribute("searchText"),
                (String) model.getAttribute("areaText"));

        model.addAttribute("page", page);
        model.addAttribute("url", "/searchResult");
        return "searchResult";
    }

    private Page<ReferenceDescription> getSearchResultReferencesPage(@CurrentUser User user, Model model, Pageable pageable,
                                                          String sortBy,
                                                          String load,
                                                          String q,
                                                          String area) {
        List<ReferenceDescription> references;

        if (area != null && area.equals("all")) {
            references = referenceSearchService.fullTextSearchPublicReferencesOnly(q, user);
            log.info("Выполнен поиск по всем публичным ссылкам сайта по запросу пользователя с uid {} с текстом {}", user.getUid(), q);
        } else {
            references = referenceSearchService.fullTextSearchReferencesByUserUid(q, user);
            log.info("Выполнен поиск по личным ссылкам пользователя с uid {} с текстом {}", user.getUid(), q);
        }

        Page<ReferenceDescription> page = new PageImpl<>(references, pageable, references.size());

        if (load != null && load.equals("all")) {
            page = new PageImpl<>(references, pageable, references.size());
        }

        if (sortBy != null) {
            page = getSortedReferences(user, pageable, references, sortBy);
        }
        return page;
    }

    private Page<ReferenceDescription> getSortedReferences(@CurrentUser User user, Pageable pageable, List<ReferenceDescription> references,
                                                @RequestParam(name = "sortBy", required = false) String sortBy) {
        Page<ReferenceDescription> page = null;
        switch (sortBy) {
            case "nameDesc":
                log.info("Сортировка результатов поиска пользователя с uid {} по имени, по-убыванию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getName).reversed());
                page = new PageImpl<>(references, pageable, references.size());
                break;
            case "nameAsc":
                log.info("Сортировка результатов поиска пользователя с uid {} по имени, по-возрастанию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getName));
                page = new PageImpl<>(references, pageable, references.size());
                break;
            case "sourceDesc":
                log.info("Сортировка результатов поиска пользователя с uid {} по источнику, по-убыванию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getSource).reversed());
                page = new PageImpl<>(references, pageable, references.size());
                break;
            case "sourceAsc":
                log.info("Сортировка результатов поиска пользователя с uid {} по источнику, по-возрастанию", user.getUid());
                references.sort(Comparator.comparing(ReferenceDescription::getSource));
                page = new PageImpl<>(references, pageable, references.size());
                break;
            case "ratingDesc":
                log.info("Сортировка результатов поиска пользователя с uid {} по рейтингу, по-убыванию", user.getUid());
                references.sort(Comparator
                        .comparing((ReferenceDescription referenceDescription) -> referenceDescription.getReference().getRating())
                        .reversed());
                page = new PageImpl<>(references, pageable, references.size());
                break;
            case "ratingAsc":
                log.info("Сортировка результатов поиска пользователя с uid {} по рейтингу, по-возрастанию", user.getUid());
                references.sort(Comparator
                        .comparing((ReferenceDescription referenceDescription) -> referenceDescription.getReference().getRating()));
                page = new PageImpl<>(references, pageable, references.size());
                break;
            default:
                log.warn("Неверный аргумент sortBy от пользователя с uid {} при попытке сортировки результатов поиска", user.getUid());
                throw new IllegalStateException("Неверный аргумент sortBy");
        }
        return page;
    }
}
