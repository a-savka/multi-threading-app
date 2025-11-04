package ru.savka.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement; // New import
import ru.savka.demo.service.ApiSourceInitializerService; // New import

@SpringBootApplication
@EnableTransactionManagement // New annotation
public class MultithreadingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultithreadingAppApplication.class, args);
	}

    @Bean
    public CommandLineRunner initApiSourceConfig(ApiSourceInitializerService apiSourceInitializerService) {
        return args -> {
            apiSourceInitializerService.initializeApiSources();
        };
    }
}
