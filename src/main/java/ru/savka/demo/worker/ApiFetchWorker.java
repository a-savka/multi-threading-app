package ru.savka.demo.worker;

import ru.savka.demo.entity.ApiSourceConfig;
import ru.savka.demo.entity.RawApiResponse;
import ru.savka.demo.entity.RawApiResponse.Status;
import ru.savka.demo.repository.RawApiResponseRepository;
import ru.savka.demo.service.ApiClientService;
import ru.savka.demo.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ApiFetchWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ApiFetchWorker.class);

    private final ApiSourceConfig sourceConfig;
    private final ApiClientService apiClientService;
    private final RawApiResponseRepository rawApiResponseRepository;
    private final LoggingService loggingService;

    public ApiFetchWorker(ApiSourceConfig sourceConfig, ApiClientService apiClientService,
                          RawApiResponseRepository rawApiResponseRepository, LoggingService loggingService) {
        this.sourceConfig = sourceConfig;
        this.apiClientService = apiClientService;
        this.rawApiResponseRepository = rawApiResponseRepository;
        this.loggingService = loggingService;
    }

    @Override
    public void run() {
        log.info("Starting fetch for source: {}", sourceConfig.getName());
        loggingService.logEvent("Starting fetch for source: " + sourceConfig.getName());

        RawApiResponse rawApiResponse = new RawApiResponse();
        rawApiResponse.setSourceId(sourceConfig.getId());
        rawApiResponse.setReceivedAt(Instant.now());
        rawApiResponse.setAttempts(0); // Initial attempt count

        try {
            String payload = apiClientService.fetch(sourceConfig.getUrlTemplate(), sourceConfig.getTimeoutMs());
            rawApiResponse.setPayload(payload);
            rawApiResponse.setStatus(Status.NEW);
            rawApiResponseRepository.save(rawApiResponse);
            log.info("Successfully fetched data for source: {}", sourceConfig.getName());
            loggingService.logEvent("Successfully fetched data for source: " + sourceConfig.getName());
        } catch (Exception e) {
            log.error("Failed to fetch data for source: {}. Error: {}", sourceConfig.getName(), e.getMessage(), e);
            loggingService.logError("Failed to fetch data for source: " + sourceConfig.getName() + ". Error: " + e.getMessage(), e);
            rawApiResponse.setStatus(Status.FAILED);
            rawApiResponse.setLastError(e.getMessage());
            rawApiResponse.setAttempts(rawApiResponse.getAttempts() + 1);
            rawApiResponseRepository.save(rawApiResponse); // Save with FAILED status
        }
    }
}
