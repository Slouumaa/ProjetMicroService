package com.esprit.microservice.gestiona.service;

import com.esprit.microservice.gestiona.entity.Categorie;
import com.esprit.microservice.gestiona.entity.Animal;
import com.esprit.microservice.gestiona.repository.AnimalRepository;
import com.esprit.microservice.gestiona.repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final CategorieRepository categorieRepository;
    private final EmailService emailService; // This should be injected

    // Modify the constructor to include the EmailService
    public AnimalService(AnimalRepository animalRepository, CategorieRepository categorieRepository, EmailService emailService) {
        this.animalRepository = animalRepository;
        this.categorieRepository = categorieRepository;
        this.emailService = emailService; // Assign the injected EmailService
    }

    public Animal saveAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal addAnimal(Animal animal, Long categorieId) {
        // Vérifier si la catégorie existe en base via son ID
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID : " + categorieId));

        // Associer la catégorie à l'animal
        animal.setCategorie(categorie);

        // Sauvegarder l'animal dans la base de données
        Animal saved = animalRepository.save(animal);

        // Send email after animal is added
        String subject = "Confirmation de votre ajout";
        String body = "Bonjour,\n\nVotre animal a bien été ajouté.\nMerci pour votre confiance.";
        emailService.sendConfirmationEmail("abdessalemchaouch9217@gmail.com", subject, body); // Use the emailService here

        return saved;
    }

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public Optional<Animal> getAnimalById(Long id) {
        return animalRepository.findById(id);
    }

    public Animal updateAnimal(Long id, Animal updatedAnimal, Long categorieId) {
        return animalRepository.findById(id)
                .map(existingAnimal -> {
                    existingAnimal.setNom(updatedAnimal.getNom());
                    existingAnimal.setRace(updatedAnimal.getRace());
                    existingAnimal.setAge(updatedAnimal.getAge());
                    existingAnimal.setSexe(updatedAnimal.getSexe());
                    existingAnimal.setDateArrivee(updatedAnimal.getDateArrivee());
                    existingAnimal.setEtatSante(updatedAnimal.getEtatSante());
                    existingAnimal.setVaccination(updatedAnimal.getVaccination());
                    existingAnimal.setDateDernierVaccin(updatedAnimal.getDateDernierVaccin());
                    existingAnimal.setSterilise(updatedAnimal.getSterilise());
                    existingAnimal.setDescription(updatedAnimal.getDescription());
                    existingAnimal.setImage(updatedAnimal.getImage());
                    existingAnimal.setTaille(updatedAnimal.getTaille());
                    existingAnimal.setPoids(updatedAnimal.getPoids());
                    existingAnimal.setOrigine(updatedAnimal.getOrigine());

                    // Vérifier et associer la nouvelle catégorie via son ID
                    Categorie categorie = categorieRepository.findById(categorieId)
                            .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID : " + categorieId));
                    existingAnimal.setCategorie(categorie);

                    return animalRepository.save(existingAnimal);
                }).orElseThrow(() -> new RuntimeException("Animal non trouvé"));
    }

    public void deleteAnimal(Long id) {
        if (!animalRepository.existsById(id)) {
            throw new RuntimeException("Animal non trouvé");
        }
        animalRepository.deleteById(id);
    }
}
