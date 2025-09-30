package ru.savka.webinar7app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.savka.webinar7app.service.DeadlockExample;
import ru.savka.webinar7app.service.LivelockExample;
import ru.savka.webinar7app.service.StarvationExample;

import java.util.Scanner;

@SpringBootApplication
public class Webinar7Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar7Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nВыберите пример для запуска:");
            System.out.println("1. Deadlock");
            System.out.println("2. Livelock");
            System.out.println("3. Starvation");
            System.out.println("4. Выход");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("--- Запуск демонстрации Deadlock ---");
                    new DeadlockExample().execute();
                    break;
                case 2:
                    System.out.println("--- Запуск демонстрации Livelock ---");
                    new LivelockExample().execute();
                    break;
                case 3:
                    System.out.println("--- Запуск демонстрации Starvation ---");
                    new StarvationExample().execute();
                    break;
                case 4:
                    System.out.println("Выход...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }
}
