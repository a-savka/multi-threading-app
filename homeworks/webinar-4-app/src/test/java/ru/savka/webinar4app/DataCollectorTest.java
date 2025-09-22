package ru.savka.webinar4app;

import org.junit.jupiter.api.Test;
import ru.savka.webinar4app.model.Item;
import ru.savka.webinar4app.service.DataCollector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataCollectorTest {

    @Test
    public void testSynchronizedDataCollector() throws InterruptedException {
        System.out.println("--- Testing Synchronized DataCollector ---");
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

        assertEquals(numThreads * itemsPerThread, dataCollector.getProcessedCount());
        assertEquals(numThreads * itemsPerThread, dataCollector.getCollectedItems().size());

        System.out.println("Processed count: " + dataCollector.getProcessedCount());
        System.out.println("Collected items: " + dataCollector.getCollectedItems().size());
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        System.out.println("--- Test Finished ---\n");
    }

    @Test
    public void testDataCollectorWithWaiting() throws InterruptedException {
        System.out.println("--- Testing DataCollector with wait() and notify() ---");
        DataCollector dataCollector = new DataCollector();
        final Object lock = new Object();
        boolean[] dataReady = {false};

        Thread producer = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Producer is collecting data...");
                for (int i = 0; i < 10; i++) {
                    dataCollector.collectItem(new Item("key" + i, "value"));
                }
                dataReady[0] = true;
                System.out.println("Producer finished collecting data and notifying consumer.");
                lock.notify();
            }
        });

        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                while (!dataReady[0]) {
                    try {
                        System.out.println("Consumer is waiting for data...");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                assertEquals(10, dataCollector.getCollectedItems().size());
                System.out.println("Consumer received data: " + dataCollector.getCollectedItems().size() + " items.");
            }
        });

        consumer.start();
        Thread.sleep(1000); // Ensure consumer starts waiting
        producer.start();

        producer.join();
        consumer.join();
        System.out.println("--- Test Finished ---\n");
    }
}