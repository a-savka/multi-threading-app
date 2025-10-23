package ru.savka.demo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Webinar13Application {

    private static final int CONSUMER_COUNT = 3;

    public static void main(String[] args) {
        // Неограниченная очередь, т.к. список URL конечен
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        // Пул потоков для производителя и потребителей
        ExecutorService executorService = Executors.newFixedThreadPool(1 + CONSUMER_COUNT);

        System.out.println("Запускаем сервер обработки URL...");

        // Запускаем одного производителя
        executorService.submit(new Producer(queue, CONSUMER_COUNT));

        // Запускаем потребителей
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executorService.submit(new Consumer(queue));
        }

        // Завершаем работу пула потоков
        // Новые задачи больше не принимаются
        executorService.shutdown();

        try {
            // Ждем завершения всех потоков (производителя и потребителей)
            if (!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
                System.err.println("Потоки не завершили работу за 10 минут. Принудительное завершение...");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Ожидание было прервано. Принудительное завершение...");
            executorService.shutdownNow();
        }

        System.out.println("Все URL обработаны. Сервер завершил работу.");
    }
}