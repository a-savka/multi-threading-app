package ru.savka.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    public void logEvent(String eventMessage) {
        log.info("APP_EVENT: {}", eventMessage);
    }

    public void logError(String errorMessage, Throwable t) {
        log.error("APP_ERROR: {}", errorMessage, t);
    }

    public void logWarning(String warningMessage) {
        log.warn("APP_WARNING: {}", warningMessage);
    }
}
