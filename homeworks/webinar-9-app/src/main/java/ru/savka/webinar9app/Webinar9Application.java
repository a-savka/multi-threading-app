package ru.savka.webinar9app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.savka.webinar9app.service.FutureExample;
import ru.savka.webinar9app.service.InvokeAllExample;
import ru.savka.webinar9app.service.PeriodicDataAggregator;
import ru.savka.webinar9app.service.ScheduledExecutorExample;

import java.util.Scanner;

@SpringBootApplication
public class Webinar9Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar9Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nВыберите пример для запуска:");
            System.out.println("1. Future и отмена задач");
            System.out.println("2. invokeAll");
            System.out.println("3. ScheduledExecutorService");
            System.out.println("4. PeriodicDataAggregator");
            System.out.println("5. Выход");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("--- Запуск демонстрации Future и отмены задач ---");
                    new FutureExample().execute();
                    break;
                case 2:
                    System.out.println("--- Запуск демонстрации invokeAll ---");
                    new InvokeAllExample().execute();
                    break;
                case 3:
                    System.out.println("--- Запуск демонстрации ScheduledExecutorService ---");
                    new ScheduledExecutorExample().execute();
                    break;
                case 4:
                    System.out.println("--- Запуск демонстрации PeriodicDataAggregator ---");
                    PeriodicDataAggregator aggregator = new PeriodicDataAggregator();
                    aggregator.start();
                    Thread.sleep(30000);
                    aggregator.stop();
                    break;
                case 5:
                    System.out.println("Выход...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }
}
