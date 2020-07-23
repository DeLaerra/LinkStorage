package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.User;
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
public class UserHomeController {
    private ReferenceService referenceService;

    @Autowired
    public UserHomeController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/userHome")
    public String showUserReferences(@CurrentUser User user, Model model, Pageable pageable,
                                     @RequestParam(name = "sortBy", required = false) String sortBy,
                                     @RequestParam(name = "load", required = false) String load,
                                     @RequestParam(name = "search", required = false) String q,
                                     @RequestParam(name = "area", required = false) String area) {
        log.info("Получен запрос об отображении ссылок пользователя с uid - {}", user.getUid());
        Page<Reference> page = getReferencesPage(user, pageable, sortBy, load);
        model.addAttribute("page", page);
        model.addAttribute("url", "/userHome");
        return "userHome";
    }

    private Page<Reference> getReferencesPage(@CurrentUser User user, Pageable pageable,
                                              @RequestParam(name = "sortBy", required = false) String sortBy,
                                              @RequestParam(name = "load", required = false) String load) {
        Page<Reference> page = referenceService.loadRefsByUserUid(user, pageable);

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

    private Page<Reference> getSortedReferences(@CurrentUser User user, Pageable pageable,
                                                @RequestParam(name = "sortBy", required = false) String sortBy) {
        Page<Reference> page;
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
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("rating").descending()));
                break;
            case "ratingAsc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("rating").ascending()));
                break;
            default:
                log.warn("Неверный аргумент sortBy от пользователя с uid {} при попытке сортировки ссылок", user.getUid());
                throw new IllegalStateException("Неверный аргумент sortBy");
        }
        return page;
    }
}
