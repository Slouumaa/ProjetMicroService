package com.esprit.microservice.gestiona.controller;

import com.esprit.microservice.gestiona.service.AnimalService;
import com.esprit.microservice.gestiona.entity.Animal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }
    @GetMapping("/search")
    public ResponseEntity<List<Animal>> searchAnimals(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minDate", required = false) String minDate,
            @RequestParam(value = "maxDate", required = false) String maxDate) {

        // Appel de la méthode du service pour récupérer tous les animaux
        List<Animal> animals = animalService.getAllAnimals();

        // Filtrer les animaux en fonction des critères de recherche
        if (name != null && !name.isEmpty()) {
            animals = animals.stream()
                    .filter(animal -> animal.getNom().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (categoryId != null) {
            animals = animals.stream()
                    .filter(animal -> animal.getCategorie() != null && animal.getCategorie().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        if (minDate != null) {
            animals = animals.stream()
                    .filter(animal -> animal.getDateArrivee() != null && animal.getDateArrivee().isAfter(LocalDate.parse(minDate)))
                    .collect(Collectors.toList());
        }

        if (maxDate != null) {
            animals = animals.stream()
                    .filter(animal -> animal.getDateArrivee() != null && animal.getDateArrivee().isBefore(LocalDate.parse(maxDate)))
                    .collect(Collectors.toList());
        }

        // Si aucun animal ne correspond aux critères de recherche
        if (animals.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Retourner les résultats filtrés
        return ResponseEntity.ok(animals);
    }
    @PostMapping(value = "/addI", consumes = {"multipart/form-data"})
    public ResponseEntity<Object> addAnimalWithImage(
            @RequestPart("animal") String animalJson, // Reçoit l'animal sous forme de JSON en String
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Créer un ObjectMapper configuré pour les types Java 8
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Désérialiser l'objet animal depuis le JSON reçu
            Animal animal = objectMapper.readValue(animalJson, Animal.class);

            // Vérifier la catégorie
            if (animal.getCategorie() != null && animal.getCategorie().getId() != null) {
                Long categorieId = animal.getCategorie().getId();

                // Upload image si présente
                if (imageFile != null && !imageFile.isEmpty()) {
                    String uploadDir = "uploads/";
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String fileName = imageFile.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(imageFile.getInputStream(), filePath);

                    animal.setImage(fileName); // stocker le nom dans l'entité
                }

                Animal savedAnimal = animalService.addAnimal(animal, categorieId);
                return ResponseEntity.ok(savedAnimal);

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erreur : Catégorie non spécifiée ou invalide.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout : " + e.getMessage());
        }
    }


    @PostMapping("/upload-image/{id}")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            Optional<Animal> optionalAnimal = animalService.getAnimalById(id);
            if (optionalAnimal.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Animal introuvable avec ID : " + id);
            }

            Animal animal = optionalAnimal.get();

            // Créer le dossier s'il n'existe pas
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Nom du fichier à enregistrer
            String fileName = imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath);

            // Mettre à jour l'animal avec le nom de l'image
            animal.setImage(fileName);
            animalService.saveAnimal(animal); // ajoute cette méthode dans AnimalService

            return ResponseEntity.ok("Image uploaded successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du téléversement : " + e.getMessage());
        }
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
    @PutMapping(value = "/updateWithImage/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Object> updateAnimalWithImage(
            @PathVariable Long id,
            @RequestPart("animal") String animalJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        try {
            // Mapper pour gérer LocalDate
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Convertir le JSON en objet Animal
            Animal animal = objectMapper.readValue(animalJson, Animal.class);

            // Vérifier la catégorie
            if (animal.getCategorie() != null && animal.getCategorie().getId() != null) {
                Long categorieId = animal.getCategorie().getId();

                // Si une nouvelle image est fournie
                if (imageFile != null && !imageFile.isEmpty()) {
                    String uploadDir = "uploads/";
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    String fileName = imageFile.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(imageFile.getInputStream(), filePath);

                    animal.setImage(fileName); // Mettre à jour le nom de l'image
                }

                // Appeler le service pour faire la mise à jour
                Animal updatedAnimal = animalService.updateAnimal(id, animal, categorieId);
                return ResponseEntity.ok(updatedAnimal);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erreur : Catégorie non spécifiée ou invalide.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour : " + e.getMessage());
        }
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
