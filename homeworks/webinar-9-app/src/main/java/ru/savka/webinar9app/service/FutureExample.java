package ru.savka.webinar9app.service;

import java.util.concurrent.*;

public class FutureExample {

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            System.out.println("Долгая задача начала выполняться...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Долгая задача была прервана");
                return "Прервано";
            }
            System.out.println("Долгая задача завершилась");
            return "Завершено";
        });

        try {
            Thread.sleep(2000);
            System.out.println("Попытка отменить долгую задачу...");
            future.cancel(true);

            if (future.isCancelled()) {
                System.out.println("Задача была успешно отменена");
            }

            if (future.isDone()) {
                try {
                    System.out.println("Результат задачи: " + future.get());
                } catch (CancellationException e) {
                    System.out.println("Не удалось получить результат, так как задача была отменена.");
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
