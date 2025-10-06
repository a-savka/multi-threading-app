package ru.savka.webinar8app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.savka.webinar8app.service.UrlProcessor;

import java.util.List;

@SpringBootApplication
public class Webinar8Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Webinar8Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Запуск обработки URL ---");

        List<String> urls = List.of(
                "https://www.google.com",
                "https://www.youtube.com",
                "https://www.facebook.com",
                "https://www.baidu.com",
                "https://www.wikipedia.org",
                "https://www.yahoo.com",
                "https://www.yandex.ru",
                "https://www.amazon.com",
                "https://www.twitter.com",
                "https://www.instagram.com",
                "https://www.linkedin.com",
                "https://www.microsoft.com",
                "https://www.apple.com",
                "https://www.netflix.com",
                "https://www.office.com",
                "https://www.twitch.tv",
                "https://www.reddit.com",
                "https://www.bing.com",
                "https://www.roblox.com",
                "https://www.duckduckgo.com"
        );

        UrlProcessor urlProcessor = new UrlProcessor();
        urlProcessor.processUrls(urls);

        System.out.println("--- Обработка URL завершена ---");
    }
}
