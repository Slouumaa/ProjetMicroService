package com.esprit.microservice.gestiona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GestionAApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionAApplication.class, args);
	}

}
