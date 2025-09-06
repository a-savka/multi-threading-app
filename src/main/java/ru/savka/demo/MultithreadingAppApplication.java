package ru.savka.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultithreadingAppApplication {

	public static void main(String[] args) {
        SpringApplication.run(MultithreadingAppApplication.class, args);
        System.out.println("hey");
	}

}
