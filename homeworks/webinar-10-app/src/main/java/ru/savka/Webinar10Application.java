package ru.savka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.savka.model.FinalProductPrice;
import ru.savka.model.Product;
import ru.savka.service.CurrencyRateService;
import ru.savka.service.ProductService;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class Webinar10Application {

    private static final Logger log = LoggerFactory.getLogger(Webinar10Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Webinar10Application.class, args);
    }

    @Bean
    public CommandLineRunner run(ProductService productService, CurrencyRateService currencyRateService, ConfigurableApplicationContext context) {
        return args -> {
            List<Integer> productIds = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 99); // 99 is a non-existent ID

            log.info("--- Запуск синхронного выполнения ---");
            long syncStart = System.currentTimeMillis();
            List<FinalProductPrice> syncResult = fetchAllDataSync(productIds, productService, currencyRateService);
            long syncEnd = System.currentTimeMillis();
            log.info("Синхронное выполнение заняло: {} мс", (syncEnd - syncStart));
            log.info("Результат:{}", System.lineSeparator() + formatResults(syncResult));

            log.info("--- Запуск асинхронного выполнения ---");
            long asyncStart = System.currentTimeMillis();
            List<FinalProductPrice> asyncResult = fetchAllDataAsync(productIds, productService, currencyRateService);
            long asyncEnd = System.currentTimeMillis();
            log.info("Асинхронное выполнение заняло: {} мс", (asyncEnd - asyncStart));
            log.info("Результат:{}", System.lineSeparator() + formatResults(asyncResult));

            // Close the application context to exit
            context.close();
        };
    }

    private String formatResults(List<FinalProductPrice> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------------------").append(System.lineSeparator());
        sb.append(String.format("| %-20s | %-10s | %-10s |", "Название", "Цена (USD)", "Цена (RUB)")).append(System.lineSeparator());
        sb.append("-------------------------------------------------").append(System.lineSeparator());
        for (FinalProductPrice price : results) {
            sb.append(String.format("| %-20s | %-10.2f | %-10.2f |", price.productName(), price.priceInUsd(), price.priceInRub())).append(System.lineSeparator());
        }
        sb.append("-------------------------------------------------").append(System.lineSeparator());
        return sb.toString();
    }

    private List<FinalProductPrice> fetchAllDataSync(List<Integer> ids, ProductService productService, CurrencyRateService currencyRateService) {
        return ids.stream()
                .map(id -> {
                    try {
                        Product product = productService.getProductById(id).orElse(null);
                        if (product == null) {
                            log.warn("Товар с ID {} не найден", id);
                            return null;
                        }
                        double rate = currencyRateService.getRubRate();
                        return new FinalProductPrice(product.name(), product.priceInUsd(), product.priceInUsd() * rate);
                    } catch (Exception e) {
                        log.error("Ошибка при обработке товара с ID {}", id, e);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<FinalProductPrice> fetchAllDataAsync(List<Integer> ids, ProductService productService, CurrencyRateService currencyRateService) {
        ExecutorService executor = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("data-fetcher-" + r.hashCode());
            return t;
        });

        List<CompletableFuture<FinalProductPrice>> futures = ids.stream()
                .map(id -> {
                    CompletableFuture<Product> productFuture = CompletableFuture.supplyAsync(() -> {
                        log.info("Получение товара {}. Поток: {}", id, Thread.currentThread().getName());
                        return productService.getProductById(id).orElse(null);
                    }, executor);

                    CompletableFuture<Double> rateFuture = CompletableFuture.supplyAsync(() -> {
                        log.info("Получение курса валют для товара {}. Поток: {}", id, Thread.currentThread().getName());
                        return currencyRateService.getRubRate();
                    }, executor);

                    return productFuture
                            .thenCombine(rateFuture, (product, rate) -> {
                                if (product == null) {
                                    log.warn("Товар с ID {} не найден", id);
                                    return null;
                                }
                                log.info("Объединение результатов для товара {}. Поток: {}", id, Thread.currentThread().getName());
                                return new FinalProductPrice(product.name(), product.priceInUsd(), product.priceInUsd() * rate);
                            })
                            .exceptionally(ex -> {
                                log.error("Не удалось получить данные для товара с ID {}. Ошибка: {}", id, ex.getMessage());
                                return null;
                            });
                })
                .collect(Collectors.toList());

        List<FinalProductPrice> results = futures.stream()
                .map(CompletableFuture::join)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        executor.shutdown();
        return results;
    }
}