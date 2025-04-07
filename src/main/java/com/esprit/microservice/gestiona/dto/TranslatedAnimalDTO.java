package com.esprit.microservice.gestiona.dto;


import lombok.Data;

@Data
public class TranslatedAnimalDTO {
    private Long id;
    private String nom;
    private String race;
    private int age;
    private String sexe;
    private String etatSante;
    private String description;
    private String origine;
}