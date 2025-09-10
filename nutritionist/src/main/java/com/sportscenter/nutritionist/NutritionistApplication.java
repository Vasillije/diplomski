package com.sportscenter.nutritionist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class NutritionistApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutritionistApplication.class, args);
	}

}
