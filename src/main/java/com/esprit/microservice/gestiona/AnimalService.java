package com.esprit.microservice.gestiona;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final CategorieRepository categorieRepository;

    public AnimalService(AnimalRepository animalRepository, CategorieRepository categorieRepository) {
        this.animalRepository = animalRepository;
        this.categorieRepository = categorieRepository;
    }

    public Animal addAnimal(Animal animal, Long categorieId) {
        // Vérifier si la catégorie existe en base via son ID
        Categorie categorie = categorieRepository.findById(categorieId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec ID : " + categorieId));

        // Associer la catégorie à l'animal
        animal.setCategorie(categorie);

        // Sauvegarder l'animal dans la base de données
        return animalRepository.save(animal);
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
