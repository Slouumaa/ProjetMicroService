package com.esprit.microservice.gestiona;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addAnimal(@RequestBody Animal animal) {
        if (animal.getCategorie() != null && animal.getCategorie().getId() != null) {
            Long categorieId = animal.getCategorie().getId();
            try {
                // Appeler la méthode pour ajouter l'animal
                Animal savedAnimal = animalService.addAnimal(animal, categorieId);
                return ResponseEntity.ok(savedAnimal);
            } catch (RuntimeException e) {
                // Gestion d'une catégorie inexistante
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erreur : La catégorie avec l'ID " + categorieId + " n'existe pas.");
            }
        }
        // Si la catégorie est absente dans le corps de la requête
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erreur : Catégorie non spécifiée dans la requête.");
    }

    // Récupérer tous les animaux
    @GetMapping("/all")
    public ResponseEntity<List<Animal>> getAllAnimals() {
        return ResponseEntity.ok(animalService.getAllAnimals());
    }

    // Récupérer un animal par ID
    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable Long id) {
        Optional<Animal> animal = animalService.getAnimalById(id);
        return animal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Mettre à jour un animal en passant l'ID de la catégorie dans le corps de la requête
    @PutMapping("/update/{id}")
    public ResponseEntity<Animal> updateAnimal(@PathVariable Long id, @RequestBody Animal animal) {
        if (animal.getCategorie() != null && animal.getCategorie().getId() != null) {
            Long categorieId = animal.getCategorie().getId();
            try {
                Animal updated = animalService.updateAnimal(id, animal, categorieId);
                return ResponseEntity.ok(updated);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.badRequest().build(); // Si catégorie est manquante
    }

    // Supprimer un animal
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.deleteAnimal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
