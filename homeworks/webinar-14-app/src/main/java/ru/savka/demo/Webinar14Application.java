package ru.savka.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;

public class Webinar14Application {

    private static final int NUMBER_OF_TASKS = 10_000;

    public static void main(String[] args) {
        System.out.println("Начинаем сравнение производительности виртуальных и платформенных потоков.");

        measure("Платформенные потоки", Webinar14Application::runPlatformThreads);
        measure("Виртуальные потоки со StructuredTaskScope", Webinar14Application::runVirtualThreadsWithScope);

        System.out.println("Сравнение производительности завершено.");
    }

    private static void runPlatformThreads() {
        ThreadFactory platformThreadFactory = Thread.ofPlatform().name("platform-thread-", 0).factory();
        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(platformThreadFactory)) {
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                executor.submit(() -> {
                    System.out.println("Выполняется задача в платформенном потоке: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(Duration.ofSeconds(1));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }
    }

    private static void runVirtualThreadsWithScope() {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().name("virtual-thread-", 0).factory();
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                scope.fork(() -> {
                    System.out.println("Выполняется задача в виртуальном потоке: " + Thread.currentThread().getName());
                    Thread.sleep(Duration.ofSeconds(1));
                    return null;
                });
            }
            scope.join();
            scope.throwIfFailed();
        } catch (Exception e) {
            // Exception handling
        }
    }

    private static void measure(String type, CheckedRunnable task) {
        System.out.println("Запускаем задачи, используя " + type + "...");
        Instant start = Instant.now();
        try {
            task.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Instant end = Instant.now();
        System.out.println("Время выполнения для " + type + ": " + Duration.between(start, end).toMillis() + " мс");
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}