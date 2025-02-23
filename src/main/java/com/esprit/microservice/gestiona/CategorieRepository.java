package com.esprit.microservice.gestiona;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategorieRepository extends JpaRepository<Categorie,Long> {
}
