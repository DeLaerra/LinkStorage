package com.innopolis.referencestorage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.service.ReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ReferenceController.
 *
 * @author Roman Khokhlov
 */
@Slf4j
@RequestMapping("/reference")
@Controller
public class ReferenceController {
    private ReferenceService referenceService;

    @Autowired
    public ReferenceController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @PostMapping("/add/{userId}")
    public String add(Reference reference,  Model model, @PathVariable Long userId){
        Reference addedRef =  referenceService.addReference(userId, reference);
        return "redirect:/userHome";
    }

    @PostMapping("/update/{refId}")
    public String updateElement(Reference reference, Model model, @PathVariable Long refId){
        log.info("Получен запрос на обновление записи ссылки: \n refId - {}, \n detail - {} ", reference.getUid(), reference);
        Reference updatedRef = referenceService.updateRef(refId, reference);
        return "redirect:/userHome";
    }

    @GetMapping("delete/{refId}")
    public String deleteRef(@PathVariable Long refId, Model model) {
        log.info("Получен запрос на удаление элемента: \n Ид - {}", refId);
        Reference refDelete = referenceService.deleteRef(refId);
        model.addAttribute("referenceDelete", refDelete);
        return "redirect:/userHome";
    }

}