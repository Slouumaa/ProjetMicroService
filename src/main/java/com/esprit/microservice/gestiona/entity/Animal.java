package com.esprit.microservice.gestiona.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String race;
    private int age;
    private String sexe;
    private LocalDate dateArrivee;

    private String etatSante;
    private Boolean vaccination;
    private LocalDate dateDernierVaccin;
    private Boolean sterilise;

    private String description;
    private String image;
    private Double taille;
    private Double poids;
    private String origine;


    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

}
