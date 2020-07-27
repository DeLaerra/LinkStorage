package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.service.ReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String addReference(ReferenceDescription reference, Model model,
                               @PathVariable Long userId,
                               @RequestParam(name = "url", required = false) String url){
        log.info("Получен запрос на добавление записи ссылки: \n userId - {}, \n reference - {} ", userId, url);
        referenceService.addReference(userId, reference, url);
        return "redirect:/userHome";
    }

    @PostMapping("/update/{refId}")
    public String updateReference(ReferenceDescription reference, Model model,
                                  @PathVariable Long refId,
                                  @RequestParam(name = "url", required = false) String url){
        log.info("Получен запрос на обновление записи ссылки: \n refId - {}, \n reference - {} ", refId, url);
        referenceService.updateReference(refId, reference, url);
        return "redirect:/userHome";
    }

    @GetMapping("delete/{refId}")
    public String deleteReference(@PathVariable Long refId, Model model) {
        log.info("Получен запрос на удаление элемента: \n refId - {}", refId);
        ReferenceDescription refDelete = referenceService.deleteReference(refId);
        model.addAttribute("referenceDelete", refDelete);
        return "redirect:/userHome";
    }

}