package ru.savka.webinar8app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.*;

public class UrlProcessor {

    public void processUrls(List<String> urls) {
        int corePoolSize = 5;
        int maxPoolSize = 10;
        long keepAliveTime = 5000;

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );

        HttpClient client = HttpClient.newHttpClient();
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        for (String url : urls) {
            completionService.submit(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(url))
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    long endTime = System.currentTimeMillis();
                    return String.format("URL: %s, Статус: %d, Время ответа: %d мс", url, response.statusCode(), (endTime - startTime));
                } catch (Exception e) {
                    long endTime = System.currentTimeMillis();
                    return String.format("URL: %s, Ошибка: %s, Время ответа: %d мс", url, e.getMessage(), (endTime - startTime));
                }
            });
        }

        for (int i = 0; i < urls.size(); i++) {
            try {
                Future<String> future = completionService.take();
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
