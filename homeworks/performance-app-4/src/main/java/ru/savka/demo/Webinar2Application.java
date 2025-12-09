package ru.savka.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Webinar2Application {

	public static void main(String[] args) {
		SpringApplication.run(Webinar2Application.class, args);
	}

}
