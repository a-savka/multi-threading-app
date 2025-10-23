package ru.savka.demo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Webinar12Application {

    public static void main(String[] args) {
        BlockingQueue<Event> queue = new LinkedBlockingQueue<>(10); // Ограниченная очередь
        AtomicInteger eventCounter = new AtomicInteger(0);

        // Создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(3); // 1 производитель, 2 потребителя

        System.out.println("Запускаем обработку событий...");

        // Запускаем производителя
        executorService.submit(new Producer(queue, eventCounter));

        // Запускаем потребителей
        executorService.submit(new Consumer(queue));
        executorService.submit(new Consumer(queue));

        // Даем системе поработать некоторое время
        try {
            Thread.sleep(10000); // Работаем 10 секунд
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Завершаем работу...");

        // Завершаем работу пула потоков
        executorService.shutdownNow(); // Прерываем все потоки
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Пул потоков не завершился вовремя.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        System.out.println("Работа завершена.");
    }
}