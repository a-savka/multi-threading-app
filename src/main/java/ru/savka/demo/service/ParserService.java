package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.repository.CurrencyRateRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import ru.savka.demo.worker.ParserWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ParserService {

    private static final Logger log = LoggerFactory.getLogger(ParserService.class);

    private final ThreadManagerService threadManagerService;
    private final RawApiResponseRepository rawApiResponseRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final LoggingService loggingService;
    private final ExternalApiProperties externalApiProperties;

    private ScheduledExecutorService parserScheduler;

    public ParserService(ThreadManagerService threadManagerService,
                         RawApiResponseRepository rawApiResponseRepository,
                         CurrencyRateRepository currencyRateRepository,
                         LoggingService loggingService,
                         ExternalApiProperties externalApiProperties) {
        this.threadManagerService = threadManagerService;
        this.rawApiResponseRepository = rawApiResponseRepository;
        this.currencyRateRepository = currencyRateRepository;
        this.loggingService = loggingService;
        this.externalApiProperties = externalApiProperties;
    }

    public void startParsing() {
        if (!threadManagerService.isParserExecutorRunning()) {
            threadManagerService.startParserExecutor();
            loggingService.logEvent("ParserService: Parsing started.");
            log.info("ParserService: Parsing started.");

            // Initialize and start the scheduler for periodic parsing
            parserScheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            parserScheduler.scheduleAtFixedRate(this::parseBatch,
                    0, externalApiProperties.getFetch().getFetchIntervalMs(), TimeUnit.MILLISECONDS); // Using fetch interval for now
        } else {
            log.warn("ParserService: Parsing is already running.");
        }
    }

    public void stopParsing() {
        if (threadManagerService.isParserExecutorRunning()) {
            threadManagerService.stopParserExecutor();
            if (parserScheduler != null) {
                parserScheduler.shutdown();
                try {
                    if (!parserScheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                        parserScheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    parserScheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            loggingService.logEvent("ParserService: Parsing stopped.");
            log.info("ParserService: Parsing stopped.");
        } else {
            log.warn("ParserService: Parsing is not running.");
        }
    }

    public void parseBatch() {
        if (threadManagerService.isParserExecutorRunning()) {
            threadManagerService.getParserExecutor().submit(
                    new ParserWorker(rawApiResponseRepository, currencyRateRepository, loggingService, externalApiProperties));
            log.debug("Submitted parser worker task.");
        } else {
            log.warn("Parser executor is not running, skipping parser worker task submission.");
        }
    }
}
