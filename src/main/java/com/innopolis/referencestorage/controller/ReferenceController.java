package com.innopolis.referencestorage.controller;

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
    public String addReference(Reference reference,  Model model, @PathVariable Long userId){
        log.info("Получен запрос на добавление записи ссылки: \n userId - {}, \n reference - {} ", userId, reference.toString());
        referenceService.addReference(userId, reference);
        return "redirect:/userHome";
    }

    @PostMapping("/update/{refId}")
    public String updateReference(Reference reference, Model model, @PathVariable Long refId){
        log.info("Получен запрос на обновление записи ссылки: \n refId - {}, \n reference - {} ", refId, reference.toString());
        referenceService.updateReference(refId, reference);
        return "redirect:/userHome";
    }

    @GetMapping("delete/{refId}")
    public String deleteReference(@PathVariable Long refId, Model model) {
        log.info("Получен запрос на удаление элемента: \n refId - {}", refId);
        Reference refDelete = referenceService.deleteReference(refId);
        model.addAttribute("referenceDelete", refDelete);
        return "redirect:/userHome";
    }

}