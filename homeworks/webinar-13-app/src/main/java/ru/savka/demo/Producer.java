package ru.savka.demo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private final BlockingQueue<String> queue;
    private final int consumerCount;
    public static final String STOP_SIGNAL = "STOP_SIGNAL";

    // Список из 20 URL для тестирования
    private final List<String> urls = Arrays.asList(
            "https://www.google.com",
            "https://www.youtube.com",
            "https://www.facebook.com",
            "https://www.wikipedia.org",
            "https://www.yahoo.com",
            "https://www.amazon.com",
            "https://www.twitter.com",
            "https://www.instagram.com",
            "https://www.linkedin.com",
            "https://www.microsoft.com",
            "https://www.apple.com",
            "https://www.netflix.com",
            "https://www.twitch.tv",
            "https://www.reddit.com",
            "https://www.github.com",
            "https://www.stackoverflow.com",
            "https://www.yandex.ru",
            "https://www.vk.com",
            "https://www.mail.ru",
            "https://www.oracle.com"
    );

    public Producer(BlockingQueue<String> queue, int consumerCount) {
        this.queue = queue;
        this.consumerCount = consumerCount;
    }

    @Override
    public void run() {
        try {
            System.out.println("Продюсер начинает добавлять URL в очередь.");
            for (String url : urls) {
                queue.put(url);
                System.out.println("Продюсер добавил URL: " + url);
                Thread.sleep(500); // Имитация задержки
            }

            // Добавляем "отравленные таблетки" для каждого потребителя
            for (int i = 0; i < consumerCount; i++) {
                queue.put(STOP_SIGNAL);
            }
            System.out.println("Продюсер закончил работу и разослал 'отравленные таблетки'.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Продюсер был прерван.");
        }
    }
}
