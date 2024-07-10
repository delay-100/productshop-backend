package com.whitedelay.productshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class ProductshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductshopApplication.class, args);
	}

}
