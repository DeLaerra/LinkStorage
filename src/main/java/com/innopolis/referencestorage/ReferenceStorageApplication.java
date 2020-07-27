package com.innopolis.referencestorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class ReferenceStorageApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(ReferenceStorageApplication.class, args);
	}

}
