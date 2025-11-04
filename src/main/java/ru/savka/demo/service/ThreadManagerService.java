package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ThreadManagerService {

    private static final Logger log = LoggerFactory.getLogger(ThreadManagerService.class);

    private final ExternalApiProperties externalApiProperties;
    private final LoggingService loggingService;

    private ExecutorService fetchExecutor;
    private ExecutorService parserExecutor;

    private final AtomicBoolean fetchRunning = new AtomicBoolean(false);
    private final AtomicBoolean parserRunning = new AtomicBoolean(false);

    public ThreadManagerService(ExternalApiProperties externalApiProperties, LoggingService loggingService) {
        this.externalApiProperties = externalApiProperties;
        this.loggingService = loggingService;
    }

    public void startFetchExecutor() {
        if (fetchRunning.compareAndSet(false, true)) {
            int maxFetchers = externalApiProperties.getFetch().getMaxConcurrentFetchers();
            fetchExecutor = Executors.newFixedThreadPool(maxFetchers);
            loggingService.logEvent("Fetch executor started with " + maxFetchers + " threads.");
            log.info("Fetch executor started with {} threads.", maxFetchers);
        } else {
            log.warn("Fetch executor is already running.");
        }
    }

    public void stopFetchExecutor() {
        if (fetchRunning.compareAndSet(true, false)) {
            shutdownExecutor(fetchExecutor, "Fetch");
            loggingService.logEvent("Fetch executor stopped.");
            log.info("Fetch executor stopped.");
        } else {
            log.warn("Fetch executor is not running.");
        }
    }

    public void startParserExecutor() {
        if (parserRunning.compareAndSet(false, true)) {
            int maxParsers = externalApiProperties.getParser().getMaxConcurrentParsers();
            parserExecutor = Executors.newFixedThreadPool(maxParsers);
            loggingService.logEvent("Parser executor started with " + maxParsers + " threads.");
            log.info("Parser executor started with {} threads.", maxParsers);
        } else {
            log.warn("Parser executor is already running.");
        }
    }

    public void stopParserExecutor() {
        if (parserRunning.compareAndSet(true, false)) {
            shutdownExecutor(parserExecutor, "Parser");
            loggingService.logEvent("Parser executor stopped.");
            log.info("Parser executor stopped.");
        } else {
            log.warn("Parser executor is not running.");
        }
    }

    public ExecutorService getFetchExecutor() {
        return fetchExecutor;
    }

    public ExecutorService getParserExecutor() {
        return parserExecutor;
    }

    public boolean isFetchExecutorRunning() {
        return fetchRunning.get();
    }

    public boolean isParserExecutorRunning() {
        return parserRunning.get();
    }

    public int getActiveFetchTasks() {
        return fetchExecutor instanceof ThreadPoolExecutor ? ((ThreadPoolExecutor) fetchExecutor).getActiveCount() : 0;
    }

    public int getQueuedFetchTasks() {
        return fetchExecutor instanceof ThreadPoolExecutor ? ((ThreadPoolExecutor) fetchExecutor).getQueue().size() : 0;
    }

    public int getActiveParserTasks() {
        return parserExecutor instanceof ThreadPoolExecutor ? ((ThreadPoolExecutor) parserExecutor).getActiveCount() : 0;
    }

    public int getQueuedParserTasks() {
        return parserExecutor instanceof ThreadPoolExecutor ? ((ThreadPoolExecutor) parserExecutor).getQueue().size() : 0;
    }

    private void shutdownExecutor(ExecutorService executor, String name) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        log.error("{} executor did not terminate.", name);
                    }
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @PreDestroy
    public void destroy() {
        stopFetchExecutor();
        stopParserExecutor();
    }
}
