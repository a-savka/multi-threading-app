package ru.savka.demo.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.entity.CurrencyRate;
import ru.savka.demo.entity.RawApiResponse;
import ru.savka.demo.entity.RawApiResponse.Status;
import ru.savka.demo.repository.CurrencyRateRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import ru.savka.demo.service.LoggingService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ParserWorker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ParserWorker.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RawApiResponseRepository rawApiResponseRepository;
    private final CurrencyRateRepository currencyRateRepository;
    private final LoggingService loggingService;
    private final int pollingBatchSize;

    private final Timer parsingTimer;
    private final Counter parsingSuccessCounter;
    private final Counter parsingFailureCounter;
    private final Counter dbRecordsInsertedCounter;
    private final Tracer tracer;

    public ParserWorker(RawApiResponseRepository rawApiResponseRepository,
                        CurrencyRateRepository currencyRateRepository,
                        LoggingService loggingService,
                        ExternalApiProperties externalApiProperties,
                        MeterRegistry meterRegistry,
                        OpenTelemetry openTelemetry) {
        this.rawApiResponseRepository = rawApiResponseRepository;
        this.currencyRateRepository = currencyRateRepository;
        this.loggingService = loggingService;
        this.pollingBatchSize = externalApiProperties.getParser().getPollingBatchSize();
        this.tracer = openTelemetry.getTracer(ParserWorker.class.getName(), "1.0.0");

        this.parsingTimer = Timer.builder("parsing.time")
                .description("Time taken to parse a raw API response")
                .tag("application", "multithreading-app")
                .register(meterRegistry);
        this.parsingSuccessCounter = Counter.builder("parsing.success.total")
                .description("Total number of successful raw API response parsings")
                .tag("application", "multithreading-app")
                .register(meterRegistry);
        this.parsingFailureCounter = Counter.builder("parsing.failure.total")
                .description("Total number of failed raw API response parsings")
                .tag("application", "multithreading-app")
                .register(meterRegistry);
        this.dbRecordsInsertedCounter = Counter.builder("db.records.inserted.total")
                .description("Total number of currency rate records inserted into the database")
                .tag("application", "multithreading-app")
                .register(meterRegistry);
    }

    @Override
    @Transactional // Ensure the whole batch processing is transactional
    public void run() {
        Span parentSpan = tracer.spanBuilder("ParserWorker.run").startSpan();
        try (Scope scope = parentSpan.makeCurrent()) {
            log.info("Starting parser worker.");
            loggingService.logEvent("Starting parser worker.");

            Span dbFetchSpan = tracer.spanBuilder("findByStatusOrderByReceivedAtAsc").startSpan();
            Pageable pageable = PageRequest.of(0, pollingBatchSize);
            List<RawApiResponse> rawResponses;
            try (Scope dbScope = dbFetchSpan.makeCurrent()) {
                rawResponses = rawApiResponseRepository.findByStatusOrderByReceivedAtAsc(Status.NEW, pageable);
                dbFetchSpan.setAttribute("response.count", rawResponses.size());
            } finally {
                dbFetchSpan.end();
            }


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

            List<RawApiResponse> responsesToUpdate = new ArrayList<>();
            for (RawApiResponse rawResponse : rawResponses) {
                Span processingSpan = tracer.spanBuilder("processSingleResponse").startSpan();
                processingSpan.setAttribute("rawResponse.id", rawResponse.getId());
                try (Scope pScope = processingSpan.makeCurrent()) {
                    // Measure parsing time
                    List<CurrencyRate> parsedRates = parsingTimer.record(() -> {
                        try {
                            return parsePayload(rawResponse);
                        } catch (Exception e) {
                            throw new RuntimeException(e); // Re-throw as unchecked for timer.record()
                        }
                    });

                    Span dbSaveRatesSpan = tracer.spanBuilder("saveCurrencyRates").startSpan();
                    try (Scope sScope = dbSaveRatesSpan.makeCurrent()) {
                        currencyRateRepository.saveAll(parsedRates);
                        dbSaveRatesSpan.setAttribute("rate.count", parsedRates.size());
                    } finally {
                        dbSaveRatesSpan.end();
                    }


                    rawResponse.setStatus(Status.PROCESSED);
                    parsingSuccessCounter.increment(); // Increment success counter
                    dbRecordsInsertedCounter.increment(parsedRates.size()); // Increment DB records counter

                    log.info("Successfully parsed and saved rates for RawApiResponse ID: {}", rawResponse.getId());
                    loggingService.logEvent("Successfully parsed and saved rates for RawApiResponse ID: " + rawResponse.getId());
                } catch (Exception e) {
                    processingSpan.recordException(e);
                    rawResponse.setStatus(Status.FAILED);
                    rawResponse.setLastError(e.getMessage());
                    rawResponse.setAttempts(rawResponse.getAttempts() + 1);
                    parsingFailureCounter.increment(); // Increment failure counter

                    log.error("Failed to parse RawApiResponse ID: {}. Error: {}", rawResponse.getId(), e.getMessage(), e);
                    loggingService.logError("Failed to parse RawApiResponse ID: " + rawResponse.getId() + ". Error: " + e.getMessage(), e);
                } finally {
                    responsesToUpdate.add(rawResponse);
                    processingSpan.end();
                }
            }

            Span dbUpdateSpan = tracer.spanBuilder("updateResponseStatuses").startSpan();
            try (Scope uScope = dbUpdateSpan.makeCurrent()) {
                rawApiResponseRepository.saveAll(responsesToUpdate);
                dbUpdateSpan.setAttribute("response.count", responsesToUpdate.size());
            } finally {
                dbUpdateSpan.end();
            }

        } finally {
            parentSpan.end();
        }
    }

    private List<CurrencyRate> parsePayload(RawApiResponse rawResponse) throws Exception {
        Span span = tracer.spanBuilder("parsePayload").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("rawResponse.id", rawResponse.getId());
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
                Object valueObj = currencyData.get("Value");
                Double value = null;
                if (valueObj instanceof Number) {
                    value = ((Number) valueObj).doubleValue();
                }


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
            span.setAttribute("parsed.rates.count", rates.size());
            return rates;
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
