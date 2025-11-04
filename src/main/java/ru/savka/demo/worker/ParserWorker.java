package ru.savka.demo.worker;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.entity.CurrencyRate;
import ru.savka.demo.entity.RawApiResponse;
import ru.savka.demo.entity.RawApiResponse.Status;
import ru.savka.demo.repository.CurrencyRateRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import ru.savka.demo.service.LoggingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParserWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ParserWorker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RawApiResponseRepository rawApiResponseRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final LoggingService loggingService;
    private final int pollingBatchSize;

    public ParserWorker(RawApiResponseRepository rawApiResponseRepository,
                        CurrencyRateRepository currencyRateRepository,
                        LoggingService loggingService,
                        ExternalApiProperties externalApiProperties) {
        this.rawApiResponseRepository = rawApiResponseRepository;
        this.currencyRateRepository = currencyRateRepository;
        this.loggingService = loggingService;
        this.pollingBatchSize = externalApiProperties.getParser().getPollingBatchSize();
    }

    @Override
    @Transactional // Ensure the whole batch processing is transactional
    public void run() {
        log.info("Starting parser worker.");
        loggingService.logEvent("Starting parser worker.");

        Pageable pageable = PageRequest.of(0, pollingBatchSize);
        List<RawApiResponse> rawResponses = rawApiResponseRepository.findByStatusOrderByReceivedAtAsc(Status.NEW, pageable);

        if (rawResponses.isEmpty()) {
            log.debug("No new raw responses to parse.");
            return;
        }

        log.info("Found {} new raw responses to parse.", rawResponses.size());
        loggingService.logEvent("Found " + rawResponses.size() + " new raw responses to parse.");

        List<Long> idsToProcess = new ArrayList<>();
        for (RawApiResponse rawResponse : rawResponses) {
            log.info("Parsing response with id: {}", rawResponse.getId());
            idsToProcess.add(rawResponse.getId());
        }

        log.info("UPDATE STATUS");
        // Mark as PROCESSING to avoid race conditions
        try {
            rawApiResponseRepository.updateStatusByIdIn(idsToProcess, Status.PROCESSING, Status.NEW);
        } catch (Exception e) {
            log.info("UPDATE ERROR: {}", e.toString());
            throw e;
        }
        log.info("AFTER UPDATE STATUS");

        for (RawApiResponse rawResponse : rawResponses) {
            try {
                List<CurrencyRate> parsedRates = parsePayload(rawResponse);
                currencyRateRepository.saveAll(parsedRates);
                rawResponse.setStatus(Status.PROCESSED);
                log.info("Successfully parsed and saved rates for RawApiResponse ID: {}", rawResponse.getId());
                loggingService.logEvent("Successfully parsed and saved rates for RawApiResponse ID: " + rawResponse.getId());
            } catch (Exception e) {
                rawResponse.setStatus(Status.FAILED);
                rawResponse.setLastError(e.getMessage());
                rawResponse.setAttempts(rawResponse.getAttempts() + 1);
                log.error("Failed to parse RawApiResponse ID: {}. Error: {}", rawResponse.getId(), e.getMessage(), e);
                loggingService.logError("Failed to parse RawApiResponse ID: " + rawResponse.getId() + ". Error: " + e.getMessage(), e);
            } finally {
                rawApiResponseRepository.save(rawResponse); // Update status (PROCESSED or FAILED)
            }
        }
    }

    private List<CurrencyRate> parsePayload(RawApiResponse rawResponse) throws Exception {
        List<CurrencyRate> rates = new ArrayList<>();
        // Parse the entire JSON response
        Map<String, Object> fullResponse = objectMapper.readValue(rawResponse.getPayload(), Map.class);

        // Extract the "Valute" object
        Map<String, Map<String, Object>> valuteMap = (Map<String, Map<String, Object>>) fullResponse.get("Valute");

        if (valuteMap == null) {
            throw new IllegalArgumentException("Valute object not found in API response.");
        }

        for (Map.Entry<String, Map<String, Object>> entry : valuteMap.entrySet()) {
            String charCode = entry.getKey(); // This is the currency code (e.g., "USD", "EUR")
            Map<String, Object> currencyData = entry.getValue();

            // Extract Nominal and Value
            Integer nominal = (Integer) currencyData.get("Nominal");
            Double value = (Double) currencyData.get("Value");

            if (nominal != null && value != null && nominal > 0) {
                BigDecimal rate = BigDecimal.valueOf(value).divide(BigDecimal.valueOf(nominal), 4, BigDecimal.ROUND_HALF_UP);

                CurrencyRate currencyRate = new CurrencyRate(
                        null, // id
                        charCode,
                        rate,
                        "RUB", // Base currency is now RUB
                        LocalDateTime.now(), // Use current time for rateDate
                        rawResponse.getSourceId(),
                        Instant.now()
                );
                rates.add(currencyRate);
            } else {
                log.warn("Invalid Nominal or Value for currency {}: Nominal={}, Value={}", charCode, nominal, value);
            }
        }
        return rates;
    }
}
