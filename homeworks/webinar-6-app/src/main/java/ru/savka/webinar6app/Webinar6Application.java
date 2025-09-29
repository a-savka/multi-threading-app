package ru.savka.webinar6app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.savka.webinar6app.service.AtomicCounter;
import ru.savka.webinar6app.service.SingletonCache;
import ru.savka.webinar6app.service.VolatileExample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Webinar6Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar6Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Демонстрация работы volatile ---");
        VolatileExample volatileExample = new VolatileExample();
        volatileExample.start();
        Thread.sleep(2000);
        volatileExample.stop();
        System.out.println("--- Демонстрация volatile завершена ---");

        System.out.println("--- Демонстрация работы AtomicInteger ---");
        AtomicCounter atomicCounter = new AtomicCounter();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executor.submit(atomicCounter::increment);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("Итоговое значение: " + atomicCounter.getValue());
        System.out.println("--- Демонстрация AtomicInteger завершена ---");

        System.out.println("---\n--- Демонстрация работы AtomicReference ---");
        SingletonCache singletonCache = new SingletonCache();
        ExecutorService cacheExecutor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            cacheExecutor.submit(singletonCache::getValue);
        }
        cacheExecutor.shutdown();
        try {
            cacheExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted during cacheExecutor termination.");
        }
        System.out.println("--- Демонстрация AtomicReference завершена ---");
    }
}
