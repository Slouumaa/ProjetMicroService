package com.esprit.microservice.gestiona;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategorieController {
    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }


    @GetMapping
    public List<Categorie> getAllCategories() {
        return categorieService.getAllCategories();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Long id) {
        Categorie categorie = categorieService.getCategorieById(id);
        return categorie != null ? ResponseEntity.ok(categorie) : ResponseEntity.notFound().build();
    }


    @PostMapping("/add")
    public ResponseEntity<Categorie> createCategorie(@RequestBody Categorie categorie) {
        Categorie savedCategorie = categorieService.saveCategorie(categorie);
        return ResponseEntity.ok(savedCategorie);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie updatedCategorie) {
        Categorie updated = categorieService.updateCategorie(id, updatedCategorie);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategorie(@PathVariable Long id) {
        boolean deleted = categorieService.deleteCategorie(id);
        return deleted ? ResponseEntity.ok("Catégorie supprimée avec succès.") : ResponseEntity.notFound().build();
    }
}
