package com.milk4u.doorstep.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DoorstepDeliveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoorstepDeliveryServiceApplication.class, args);
	}

}
