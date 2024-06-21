package com.whitedelay.productshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ProductshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductshopApplication.class, args);
	}

}
