package ru.savka.webinar5app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.savka.webinar5app.service.BoundedBuffer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Webinar5Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar5Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Начало демонстрации BoundedBuffer ---");

        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Producers
        for (int i = 0; i < 2; i++) {
            final int producerId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 20; j++) {
                        int item = producerId * 100 + j;
                        buffer.put(item);
                        System.out.println("Producer " + producerId + " положил: " + item);
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Consumers
        for (int i = 0; i < 3; i++) {
            final int consumerId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 13; j++) {
                        Integer item = buffer.take();
                        System.out.println("Consumer " + consumerId + " извлек: " + item);
                        TimeUnit.MILLISECONDS.sleep(200);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("--- Демонстрация завершена ---");
    }
}
