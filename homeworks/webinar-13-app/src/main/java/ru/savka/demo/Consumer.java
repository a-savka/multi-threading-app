package ru.savka.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumer implements Runnable {
    private final BlockingQueue<String> queue;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String urlString = queue.take();

                // Проверяем на "отравленную таблетку"
                if (Producer.STOP_SIGNAL.equals(urlString)) {
                    System.out.println("Консьюмер получил 'сигнал остановки' и завершает работу.");
                    break; // Выходим из цикла
                }

                System.out.println("Консьюмер обрабатывает URL: " + urlString);
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000); // 5 секунд таймаут на подключение
                    connection.setReadTimeout(5000);    // 5 секунд таймаут на чтение

                    // Читаем содержимое страницы
                    StringBuilder content = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line);
                            // Прерываем чтение, если заголовок уже найден, для оптимизации
                            if (line.contains("</title>")) {
                                break;
                            }
                        }
                    }

                    // Извлекаем заголовок с помощью регулярного выражения
                    Pattern pattern = Pattern.compile("<title>(.*?)</title>");
                    Matcher matcher = pattern.matcher(content.toString());

                    if (matcher.find()) {
                        System.out.println("  -> Заголовок для " + urlString + ": " + matcher.group(1));
                    } else {
                        System.out.println("  -> Заголовок для " + urlString + " не найден.");
                    }

                } catch (Exception e) {
                    System.err.println("  -> Ошибка при обработке URL " + urlString + ": " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Консьюмер был прерван.");
        }
    }
}
