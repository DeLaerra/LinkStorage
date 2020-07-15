package com.innopolis.referencestorage.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.innopolis.referencestorage.domain.Role;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ReferenceRepo;
import com.innopolis.referencestorage.service.ReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReferenceController.
 *
 * @author Roman Khokhlov
 */
@Slf4j
@RequestMapping("/reference")
@PreAuthorize("hasAuthority('ADMIN')")
@RestController
public class ReferenceController {
    private ReferenceService referenceService;

    @Autowired
    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @PostMapping(
            path = "{userId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public JsonNode add(@PathVariable Long userId, @RequestBody JsonNode detail) throws JsonProcessingException {
        return referenceService.addReference(userId, detail);
    }

    @GetMapping("{refId}")
    public JsonNode getRef( @PathVariable("refId") Long refId) {
        return referenceService.getRef(refId);
    }

    @PatchMapping(
            path = "{refId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public JsonNode updateElement(@PathVariable Long refId,@RequestBody JsonNode detail) throws JsonProcessingException {
        log.info("Получен запрос на обновление записи ссылки: \n refId - {}, \n detail - {} ", refId, detail);
        return referenceService.updateRef(refId, detail);
    }

    @DeleteMapping("/{refId}")
    public JsonNode deleteRef(@PathVariable Long refId) {
        log.info("Получен запрос на удаление элемента: \n Ид - {}", refId);
        return referenceService.deleteRef(refId);
    }

}