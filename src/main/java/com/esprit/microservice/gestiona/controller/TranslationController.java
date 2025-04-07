package com.esprit.microservice.gestiona.controller;

import com.esprit.microservice.gestiona.dto.TranslationRequest;
import com.esprit.microservice.gestiona.dto.TranslationResponse;
import com.esprit.microservice.gestiona.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @PostMapping("/translate")
    public String translate(@RequestParam String text, @RequestParam String toLanguage) {
        return translationService.translate(text, toLanguage);
    }
}