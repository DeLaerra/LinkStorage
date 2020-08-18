package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.config.CurrentUser;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class ReferenceSortingService {
    private ReferenceService referenceService;

    @Autowired
    public ReferenceSortingService(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }


    public Page<ReferenceDescription> getSortedReferences(User user, Pageable pageable, String sortBy, String load) {
        log.info("Получен запрос на сортировку ссылок пользователя с uid - {}", user.getUid());
        Page<ReferenceDescription> page;
        int pageSize;
        int pageNumber;

        if (sortBy == null || sortBy.equals("")) {
            sortBy = "default";
        }

        if (load != null && load.equals("all")) {
            log.info("Получен запрос на отображение всех ссылок пользователя с uid - {}", user.getUid());
            pageSize = Integer.MAX_VALUE;
            pageNumber = 0;
        } else {
            pageSize = pageable.getPageSize();
            pageNumber = pageable.getPageNumber();
        }

        switch (sortBy) {
            case "default":
                log.info("Сортировка ссылок пользователя с uid {} по дате, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("additionDate").descending()));
                break;

            case "nameDesc":
                log.info("Сортировка ссылок пользователя с uid {} по имени, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("name").descending()));
                break;
            case "nameAsc":
                log.info("Сортировка ссылок пользователя с uid {} по имени, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("name").ascending()));
                break;
            case "sourceDesc":
                log.info("Сортировка ссылок пользователя с uid {} по источнику, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("source").descending()));
                break;
            case "sourceAsc":
                log.info("Сортировка ссылок пользователя с uid {} по источнику, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("source").ascending()));
                break;
            case "ratingDesc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-убыванию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("reference.rating").descending()));
                break;
            case "ratingAsc":
                log.info("Сортировка ссылок пользователя с uid {} по рейтингу, по-возрастанию", user.getUid());
                page = referenceService.loadRefsByUserUid(user,
                        PageRequest.of(pageNumber, pageSize, Sort.by("reference.rating").ascending()));
                break;
            default:
                log.warn("Неверный аргумент sortBy от пользователя с uid {} при попытке сортировки ссылок", user.getUid());
                throw new IllegalStateException("Неверный аргумент sortBy");
        }
        return page;
    }


    public Page<ReferenceDescription> getSortedSearchResultReferences(@CurrentUser User user, Pageable pageable,
                                                                      List<ReferenceDescription> references,
                                                                      String sortBy, String load) {
        Page<ReferenceDescription> page;
        int pageSize;
        int pageNumber;

        if (load != null && load.equals("all")) {
            log.info("Получен запрос на отображение всех ссылок пользователя с uid - {}", user.getUid());
            pageSize = Integer.MAX_VALUE;
            pageNumber = 0;
        } else {
            pageSize = pageable.getPageSize();
            pageNumber = pageable.getPageNumber();
        }

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        int total = references.size();
        int start = Math.toIntExact(pageRequest.getOffset());
        int end = (start + pageRequest.getPageSize()) > references.size() ? references.size() : (start + pageRequest.getPageSize());

        if (sortBy == null || sortBy.equals("")) {sortBy = "default";}
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
