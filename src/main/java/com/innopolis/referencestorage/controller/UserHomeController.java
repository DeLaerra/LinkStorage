package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserHomeController {
    private ReferenceService referenceService;

    @Autowired
    public UserHomeController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/userHome")
    public String showUserRefsPage(@CurrentUser User user, Model model, Pageable pageable, @RequestParam(defaultValue = "") String sortBy,
                                   @RequestParam (required = false) String load) {
        Page page;
        if (sortBy.equals("rating")) {
            page = referenceService.loadSortedUserRefs(user,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).descending()));
        } else if (sortBy.isEmpty()) {
            page = referenceService.loadSortedUserRefs(user, pageable);
        } else if (load.equals("all")) {
            page = referenceService.loadSortedUserRefs(user, Pageable.unpaged());
        } else {
            page = referenceService.loadSortedUserRefs(user,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).ascending()));
        }

        if (!page.isEmpty()) {
            model.addAttribute("page", page);
            model.addAttribute("url", "/userHome");
        }
        return "userHome";
    }
}
