package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.service.ReferenceSearchService;
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
    private ReferenceSearchService referenceSearchService;

    @Autowired
    public UserHomeController(ReferenceService referenceService, ReferenceSearchService referenceSearchService) {
        this.referenceService = referenceService;
        this.referenceSearchService = referenceSearchService;
    }

    @GetMapping("/userHome")
    public String showUserRefsPage(@CurrentUser User user, Model model, Pageable pageable,
                                   @RequestParam(name = "sortBy", required = false) String sortBy,
                                   @RequestParam(name = "load", required = false) String load,
                                   @RequestParam(name = "search", required = false) String q,
                                   @RequestParam(name = "area", required = false) String area) {
        Page<Reference> page = null;

        if (q != null && !q.equals("")) {
            page = getSearchResultReferencesPage(user, pageable, sortBy, load, q, area);
        } else {
            page = getReferencesPage(user, pageable, sortBy, load);
        }

        if (!page.isEmpty()) {
            model.addAttribute("page", page);
            model.addAttribute("url", "/userHome");
        }
        return "userHome";
    }

    private Page<Reference> getReferencesPage(@CurrentUser User user, Pageable pageable,
                                              @RequestParam(name = "sortBy", required = false) String sortBy,
                                              @RequestParam(name = "load", required = false) String load) {
        Page<Reference> page = referenceService.loadRefsByUserUid(user, pageable);

        if (load != null && load.equals("all")) {
            page = referenceService.loadRefsByUserUid(user,
                    PageRequest.of(0, pageable.getPageSize(), Sort.by("rating").descending()));
        } else if (sortBy != null) {
            if (sortBy.equals("rating")) {
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).descending()));
            } else {
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).ascending()));
            }
        }
        return page;
    }

    private Page<Reference> getSearchResultReferencesPage(@CurrentUser User user, Pageable pageable,
                                                          @RequestParam(name = "sortBy", required = false) String sortBy,
                                                          @RequestParam(name = "load", required = false) String load,
                                                          @RequestParam(name = "search", required = false) String q,
                                                          @RequestParam(name = "area", required = false) String area) {
        Page<Reference> page = null;
        if (area != null && area.equals("all")) {
            page = referenceSearchService.fullTextSearchPublicOnly(q, pageable);
        } else {
            page = referenceSearchService.fullTextSearchByUserUid(q, user, pageable);
        }

        if (load != null && load.equals("all")) {
            if (area != null && area.equals("all")) {
                page = referenceSearchService.fullTextSearchPublicOnly(q,
                        PageRequest.of(0, pageable.getPageSize(), Sort.by("rating").ascending()));
            } else {
                page = referenceSearchService.fullTextSearchByUserUid(q, user,
                        PageRequest.of(0, pageable.getPageSize(), Sort.by("rating").ascending()));
            }
        } else if (sortBy != null) {
            if (sortBy.equals("rating")) {
                if (area != null && area.equals("all")) {
                    page = referenceSearchService.fullTextSearchPublicOnly(q,
                            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).descending()));
                } else {
                    page = referenceSearchService.fullTextSearchByUserUid(q, user,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).descending()));
                }
            } else {
                if (area != null && area.equals("all")) {
                    page = referenceSearchService.fullTextSearchPublicOnly(q,
                            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).ascending()));
                } else {
                    page = referenceSearchService.fullTextSearchByUserUid(q, user,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy).ascending()));
                }
            }
        }
        return page;
    }
}
