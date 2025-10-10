package ru.savka.webinar9app.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorExample {

    public void execute() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            System.out.println("Периодическая задача: проверка статуса системы...");
        }, 0, 5, TimeUnit.SECONDS);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
