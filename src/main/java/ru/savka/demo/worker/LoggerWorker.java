package ru.savka.demo.worker;

import ru.savka.demo.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(LoggerWorker.class);

    private final LoggingService loggingService;
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public LoggerWorker(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public void addLogMessage(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Failed to add log message to queue: {}", message, e);
        }
    }

    public void stop() {
        running = false;
        // Interrupt if waiting on queue
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        log.info("LoggerWorker started.");
        loggingService.logEvent("LoggerWorker started.");

        while (running || !logQueue.isEmpty()) {
            try {
                String message = logQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (message != null) {
                    loggingService.logEvent(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("LoggerWorker interrupted while waiting for log messages.");
                // If interrupted, check running flag again and process remaining queue
            } catch (Exception e) {
                log.error("Error in LoggerWorker: {}", e.getMessage(), e);
            }
        }
        log.info("LoggerWorker stopped.");
        loggingService.logEvent("LoggerWorker stopped.");
    }
}
