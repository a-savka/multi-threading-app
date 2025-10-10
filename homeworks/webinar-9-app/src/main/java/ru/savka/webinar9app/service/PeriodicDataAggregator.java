package ru.savka.webinar9app.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class PeriodicDataAggregator {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService workerPool = Executors.newFixedThreadPool(5);

    public void start() {
        scheduler.scheduleAtFixedRate(this::aggregateData, 0, 10, TimeUnit.SECONDS);
    }

    private void aggregateData() {
        System.out.println("--- Агрегация данных началась ---");
        List<String> entityIds = List.of("USD", "EUR", "GBP", "JPY", "CHF");

        for (String entityId : entityIds) {
            workerPool.submit(() -> {
                try {
                    String data = fetchData(entityId);
                    System.out.println("Успешно получены данные для " + entityId + ": " + data);
                } catch (Exception e) {
                    System.err.println("Ошибка при получении данных для " + entityId + ": " + e.getMessage());
                }
            });
        }
    }

    private String fetchData(String entityId) throws Exception {
        int duration = new Random().nextInt(2000) + 500;
        Thread.sleep(duration);
        if (new Random().nextBoolean()) {
            throw new Exception("Не удалось получить данные");
        }
        return "Курс " + entityId + " = " + (new Random().nextDouble() * 100);
    }

    public void stop() {
        scheduler.shutdown();
        workerPool.shutdown();
    }
}
