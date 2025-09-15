package ru.savka.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootApplication
public class Webinar3Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar3Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Начало выполнения программы");

        List<Integer> numbers = new Random().ints(1_000_000).boxed().collect(Collectors.toList());

        // Последовательный стрим
        long startTime = System.currentTimeMillis();
        long sumSequential = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToLong(n -> n * 2)
                .sum();
        long endTime = System.currentTimeMillis();
        System.out.println("Сумма (последовательно): " + sumSequential);
        System.out.println("Время выполнения (последовательно): " + (endTime - startTime) + " мс");

        // Параллельный стрим
        startTime = System.currentTimeMillis();
        long sumParallel = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToLong(n -> n * 2)
                .sum();
        endTime = System.currentTimeMillis();
        System.out.println("Сумма (параллельно): " + sumParallel);
        System.out.println("Время выполнения (параллельно): " + (endTime - startTime) + " мс");

        System.out.println("Завершение выполнения программы");
    }
}
