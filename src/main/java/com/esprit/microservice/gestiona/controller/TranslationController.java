package com.esprit.microservice.gestiona.controller;

import com.esprit.microservice.gestiona.dto.TranslatedAnimalDTO;
import com.esprit.microservice.gestiona.dto.TranslationRequest;
import com.esprit.microservice.gestiona.dto.TranslationResponse;
import com.esprit.microservice.gestiona.entity.Animal;
import com.esprit.microservice.gestiona.repository.AnimalRepository;
import com.esprit.microservice.gestiona.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class TranslationController {
    @Autowired
    private AnimalRepository animalRepository;



    @Autowired
    private TranslationService translationService;

    @PostMapping("/translate")
    public String translate(@RequestParam String text, @RequestParam String toLanguage) {
        return translationService.translate(text, toLanguage);
    }
    @PostMapping("/translate-animal")
    public Map<String, String> translateAnimal(@RequestBody TranslationRequest request) {
        Map<String, String> translated = new HashMap<>();
        translated.put("nom", translationService.translate(request.getNom(), request.getToLanguage()));
        translated.put("race", translationService.translate(request.getRace(), request.getToLanguage()));
        translated.put("sexe", translationService.translate(request.getSexe(), request.getToLanguage()));
        translated.put("etatSante", translationService.translate(request.getEtatSante(), request.getToLanguage()));
        translated.put("description", translationService.translate(request.getDescription(), request.getToLanguage()));
        translated.put("origine", translationService.translate(request.getOrigine(), request.getToLanguage()));
        return translated;
    }
    @GetMapping("/animals/{id}/translate")
    public ResponseEntity<?> translateAnimal(
            @PathVariable Long id,
            @RequestParam String toLanguage
    ) {
        Optional<Animal> animalOpt = animalRepository.findById(id);
        if (animalOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Animal not found");
        }

        Animal animal = animalOpt.get();
        TranslatedAnimalDTO translated = new TranslatedAnimalDTO();
        translated.setId(animal.getId());
        translated.setAge(animal.getAge());

        // Traduction des champs textuels
        translated.setNom(translationService.translate(animal.getNom(), toLanguage));
        translated.setRace(translationService.translate(animal.getRace(), toLanguage));
        translated.setSexe(translationService.translate(animal.getSexe(), toLanguage));
        translated.setEtatSante(translationService.translate(animal.getEtatSante(), toLanguage));
        translated.setDescription(translationService.translate(animal.getDescription(), toLanguage));
        translated.setOrigine(translationService.translate(animal.getOrigine(), toLanguage));

        return ResponseEntity.ok(translated);
    }

}