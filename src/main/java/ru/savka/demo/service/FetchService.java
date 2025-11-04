package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.entity.ApiSourceConfig;
import ru.savka.demo.repository.ApiSourceConfigRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import ru.savka.demo.worker.ApiFetchWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class FetchService {

    private static final Logger log = LoggerFactory.getLogger(FetchService.class);

    private final ThreadManagerService threadManagerService;
    private final ApiSourceConfigRepository apiSourceConfigRepository;
    private final ApiClientService apiClientService;
    private final RawApiResponseRepository rawApiResponseRepository;
    private final LoggingService loggingService;
    private final ExternalApiProperties externalApiProperties;

    private ScheduledExecutorService fetchScheduler; // For periodic fetching

    public FetchService(ThreadManagerService threadManagerService,
                        ApiSourceConfigRepository apiSourceConfigRepository,
                        ApiClientService apiClientService,
                        RawApiResponseRepository rawApiResponseRepository,
                        LoggingService loggingService,
                        ExternalApiProperties externalApiProperties) {
        this.threadManagerService = threadManagerService;
        this.apiSourceConfigRepository = apiSourceConfigRepository;
        this.apiClientService = apiClientService;
        this.rawApiResponseRepository = rawApiResponseRepository;
        this.loggingService = loggingService;
        this.externalApiProperties = externalApiProperties;
    }

    public void startFetching() {
        if (!threadManagerService.isFetchExecutorRunning()) {
            threadManagerService.startFetchExecutor();
            loggingService.logEvent("FetchService: Fetching started.");
            log.info("FetchService: Fetching started.");

            // Initialize and start the scheduler for periodic fetching
            fetchScheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
            fetchScheduler.scheduleAtFixedRate(this::fetchFromAllSources,
                    0, externalApiProperties.getFetch().getFetchIntervalMs(), TimeUnit.MILLISECONDS);
        } else {
            log.warn("FetchService: Fetching is already running.");
        }
    }

    public void stopFetching() {
        if (threadManagerService.isFetchExecutorRunning()) {
            threadManagerService.stopFetchExecutor();
            if (fetchScheduler != null) {
                fetchScheduler.shutdown();
                try {
                    if (!fetchScheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                        fetchScheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    fetchScheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            loggingService.logEvent("FetchService: Fetching stopped.");
            log.info("FetchService: Fetching stopped.");
        } else {
            log.warn("FetchService: Fetching is not running.");
        }
    }

    public void submitImmediateFetch(String sourceId) {
        if (threadManagerService.isFetchExecutorRunning()) {
            apiSourceConfigRepository.findById(sourceId).ifPresent(sourceConfig -> {
                threadManagerService.getFetchExecutor().submit(
                        new ApiFetchWorker(sourceConfig, apiClientService, rawApiResponseRepository, loggingService));
                log.info("Submitted immediate fetch for source: {}", sourceId);
                loggingService.logEvent("Submitted immediate fetch for source: " + sourceId);
            });
        } else {
            log.warn("Fetch executor is not running. Cannot submit immediate fetch for source: {}", sourceId);
        }
    }

    // This method will be called periodically by the scheduler
    private void fetchFromAllSources() {
        if (threadManagerService.isFetchExecutorRunning()) {
            List<ApiSourceConfig> enabledSources = apiSourceConfigRepository.findAll(); // Assuming all are enabled for now
            for (ApiSourceConfig sourceConfig : enabledSources) {
                // Update lastChecked timestamp
                sourceConfig.setLastChecked(Instant.now());
                apiSourceConfigRepository.save(sourceConfig);

                threadManagerService.getFetchExecutor().submit(
                        new ApiFetchWorker(sourceConfig, apiClientService, rawApiResponseRepository, loggingService));
            }
            log.debug("Scheduled fetch tasks submitted for {} sources.", enabledSources.size());
        } else {
            log.warn("Fetch executor is not running, skipping scheduled fetch tasks.");
        }
    }
}
