package com.esprit.microservice.gestiona.service;

import com.esprit.microservice.gestiona.dto.TranslationRequest;
import com.esprit.microservice.gestiona.dto.TranslationResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
@Service
public class TranslationService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Clé API et URL de l'endpoint
    @Value("${microsoft.translator.api.key}")
    private String apiKey;

    @Value("${microsoft.translator.api.endpoint}")
    private String endpoint;

    public String translate(String text, String toLanguage) {
        // URL de l'API
        String url = endpoint + "/translate?api-version=3.0&to=" + toLanguage;

        // Configuration des headers de la requête
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Ocp-Apim-Subscription-Key", apiKey);
        headers.set("Ocp-Apim-Subscription-Region", "North Europe");  

        // Corps de la requête
        String requestBody = "[{\"Text\":\"" + text + "\"}]";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Appel de l'API Microsoft Translator
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Log de la réponse complète pour déboguer
        System.out.println("Response: " + response.getBody());

        // Récupérer la traduction de la réponse
        String responseBody = response.getBody();
        JSONObject jsonResponse = new JSONObject(responseBody);

        // Assure-toi que la clé "translations" existe
        if (jsonResponse.has("translations") && jsonResponse.getJSONArray("translations").length() > 0) {
            return jsonResponse.getJSONArray("translations").getJSONObject(0).getString("text");
        } else {
            return "Error: No translation found";
        }
    }

}