package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.util.RetryUtils;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class ApiClientService {

    private final WebClient.Builder webClientBuilder;
    private final ExternalApiProperties externalApiProperties;
    private final Semaphore requestSemaphore;
    private final ScheduledExecutorService scheduler;

    public ApiClientService(WebClient.Builder webClientBuilder, ExternalApiProperties externalApiProperties) {
        this.webClientBuilder = webClientBuilder;
        this.externalApiProperties = externalApiProperties;

        int apiRateLimit = externalApiProperties.getFetch().getApiRateLimitPerSecond();
        this.requestSemaphore = new Semaphore(apiRateLimit);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(() -> requestSemaphore.release(apiRateLimit), 1, 1, TimeUnit.SECONDS);
    }

    public String fetch(String url, int timeoutMs, int maxAttempts, long baseBackoffMs) throws Exception {
        requestSemaphore.acquire();
        try {
            return RetryUtils.retry(() -> {
                WebClient webClient = webClientBuilder.baseUrl(url)
                        .build();

                return webClient.get()
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofMillis(timeoutMs))
                        .block(); // Blocking for simplicity in this context
            }, maxAttempts, baseBackoffMs);
        } finally {
        }
    }

    public String fetch(String url, int timeoutMs) throws Exception {
        ru.savka.demo.config.properties.ExternalApiProperties.Retry retryProps = externalApiProperties.getFetch().getRetry();
        return fetch(url, timeoutMs, retryProps.getMaxAttempts(), retryProps.getBaseBackoffMs());
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
