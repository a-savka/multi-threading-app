package ru.savka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Performance2Application {

    private static final List<Object> memoryLeak = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Performance2Application.class, args);
        System.out.println("Приложение запущено. Начинаем симуляцию утечки памяти.");

        while (true) {
            for (int i = 0; i < 1000; i++) {
                memoryLeak.add(new byte[1024 * 10]); // 10 KB
            }
            System.out.println("Добавлено 1000 объектов в список. Текущий размер списка: " + memoryLeak.size());
            Thread.sleep(1000);
        }
    }
}
