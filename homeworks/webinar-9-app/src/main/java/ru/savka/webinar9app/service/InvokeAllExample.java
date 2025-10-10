package ru.savka.webinar9app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class InvokeAllExample {

    public void execute() {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            tasks.add(() -> {
                int duration = new Random().nextInt(2000) + 1000;
                System.out.println("Задача " + taskId + " будет выполняться " + duration + " мс");
                Thread.sleep(duration);
                return "Результат задачи " + taskId;
            });
        }

        try {
            List<Future<String>> results = executor.invokeAll(tasks);

            for (Future<String> result : results) {
                System.out.println(result.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
