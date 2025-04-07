package com.esprit.microservice.gestiona.repository;

import com.esprit.microservice.gestiona.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal,Long> {
}
