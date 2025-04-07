package com.esprit.microservice.gestiona.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TranslationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${microsoft.translator.api.key}")
    private String apiKey;

    @Value("${microsoft.translator.api.endpoint}")
    private String endpoint;

    public String translate(String text, String toLanguage) {
        try {
            String url = endpoint + "/translate?api-version=3.0&to=" + toLanguage;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Ocp-Apim-Subscription-Key", apiKey);
            headers.set("Ocp-Apim-Subscription-Region", "northeurope");

            String requestBody = "[{\"Text\":\"" + text + "\"}]";

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Log de la réponse pour déboguer
            System.out.println("Response: " + response.getBody());

            String responseBody = response.getBody();

            // Convertir la réponse en tableau JSON
            JSONArray jsonArray = new JSONArray(responseBody);

            // Assurez-vous que le tableau n'est pas vide et récupérez la traduction
            if (jsonArray.length() > 0) {
                JSONObject firstElement = jsonArray.getJSONObject(0);
                if (firstElement.has("translations") && firstElement.getJSONArray("translations").length() > 0) {
                    return firstElement.getJSONArray("translations").getJSONObject(0).getString("text");
                }
            }
            return "Error: No translation found";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while translating: " + e.getMessage();
        }
    }
}
