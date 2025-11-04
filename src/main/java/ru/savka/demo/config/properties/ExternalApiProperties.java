package ru.savka.demo.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class ExternalApiProperties {

    private List<String> supportedCurrencies;
    private List<ApiSource> apiSources;
    private Fetch fetch;
    private Parser parser;

    public Fetch getFetch() {
        return fetch;
    }
    public Parser getParser() {
        return parser;
    }

    @Data
    public static class ApiSource {
        private String id;
        private String name;
        private String url;
        private int timeoutMs;
    }

    @Data
    public static class Fetch {
        public int getMaxConcurrentFetchers() {
            return maxConcurrentFetchers;
        }

        public int getFetchIntervalMs() {
            return fetchIntervalMs;
        }

        public int getApiRateLimitPerSecond() {
            return apiRateLimitPerSecond;
        }

        public Retry getRetry() {
            return retry;
        }

        private int maxConcurrentFetchers;
        private int fetchIntervalMs;
        private int apiRateLimitPerSecond; // New field
        private Retry retry;
    }

    @Data
    public static class Retry {
        public int getBaseBackoffMs() {
            return baseBackoffMs;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }
        private int maxAttempts;
        private int baseBackoffMs;
    }

    @Data
    public static class Parser {
        public int getMaxConcurrentParsers() {
            return maxConcurrentParsers;
        }

        public int getPollingBatchSize() {
            return pollingBatchSize;
        }

        private int maxConcurrentParsers;
        private int pollingBatchSize;
    }
}
