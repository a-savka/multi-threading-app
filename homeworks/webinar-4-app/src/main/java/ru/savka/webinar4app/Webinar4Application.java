package ru.savka.webinar4app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.savka.webinar4app.model.Item;
import ru.savka.webinar4app.service.DataCollector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Webinar4Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar4Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Старт демонстрации DataCollector ---");
        testSynchronizedDataCollector();
        testDataCollectorWithWaiting();
        System.out.println("--- Демонстрация завершена ---");
    }

    public static void testSynchronizedDataCollector() throws InterruptedException {
        System.out.println("--- Проверка синхронного DataCollector ---");
        DataCollector dataCollector = new DataCollector();
        int numThreads = 10;
        int itemsPerThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    String key = Thread.currentThread().getName() + "-" + j;
                    Item item = new Item(key, "value");
                    dataCollector.collectItem(item);
                    dataCollector.incrementProcessed();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();

        System.out.println("Обработано записей: " + dataCollector.getProcessedCount());
        System.out.println("Собрано записей: " + dataCollector.getCollectedItems().size());
        System.out.println("Время выполнения: " + (endTime - startTime) + "ms");
        System.out.println("--- Тест завершен ---\n");
    }

    public static void testDataCollectorWithWaiting() throws InterruptedException {
        System.out.println("--- Тестирование DataCollector с использованием wait() и notify() ---");
        DataCollector dataCollector = new DataCollector();
        final Object lock = new Object();
        boolean[] dataReady = {false};

        Thread producer = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Producer собирает данные...");
                for (int i = 0; i < 10; i++) {
                    dataCollector.collectItem(new Item("key" + i, "value"));
                }
                dataReady[0] = true;
                System.out.println("Producer завершил сбор данных и нотификацию консьюмера.");
                lock.notify();
            }
        });

        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                while (!dataReady[0]) {
                    try {
                        System.out.println("Consumer ожидает данные...");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Consumer получил данные: " + dataCollector.getCollectedItems().size() + " .");
            }
        });

        consumer.start();
        Thread.sleep(1000); // Ensure consumer starts waiting
        producer.start();

        producer.join();
        consumer.join();
        System.out.println("--- Тест завершен ---\n");
    }
}
