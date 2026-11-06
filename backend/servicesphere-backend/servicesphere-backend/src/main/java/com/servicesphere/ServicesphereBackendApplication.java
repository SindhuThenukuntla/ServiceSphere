package com.servicesphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ServicesphereBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicesphereBackendApplication.class, args);
	}

}
