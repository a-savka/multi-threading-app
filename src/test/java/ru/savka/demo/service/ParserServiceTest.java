package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.repository.CurrencyRateRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class ParserServiceTest {

    @Autowired
    private ParserService parserService;

    @MockBean
    private ThreadManagerService threadManagerService;
    @MockBean
    private RawApiResponseRepository rawApiResponseRepository;
    @MockBean
    private CurrencyRateRepository currencyRateRepository;
    @MockBean
    private LoggingService loggingService;
    @MockBean
    private ExternalApiProperties externalApiProperties;

    private ExternalApiProperties.Fetch mockFetchProperties; // Needed for fetchIntervalMs in ParserService

    @BeforeEach
    void setUp() {
        // Mock ExternalApiProperties for fetchIntervalMs used in ParserService scheduler
        mockFetchProperties = new ExternalApiProperties.Fetch();
        mockFetchProperties.setFetchIntervalMs(100); // Short interval for testing
        when(externalApiProperties.getFetch()).thenReturn(mockFetchProperties);

        // Mock ThreadManagerService
        when(threadManagerService.isParserExecutorRunning()).thenReturn(false); // Initially not running
        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getParserExecutor()).thenReturn(mockExecutorService);
    }

    @Test
    void testStartParsing() {
        parserService.startParsing();
        verify(threadManagerService, times(1)).startParserExecutor();
        verify(loggingService, times(1)).logEvent("ParserService: Parsing started.");
        assertTrue(threadManagerService.isParserExecutorRunning()); // Should be true after start
    }

    @Test
    void testStopParsing() {
        // Simulate running state
        when(threadManagerService.isParserExecutorRunning()).thenReturn(true);
        parserService.stopParsing();
        verify(threadManagerService, times(1)).stopParserExecutor();
        verify(loggingService, times(1)).logEvent("ParserService: Parsing stopped.");
        assertFalse(threadManagerService.isParserExecutorRunning()); // Should be false after stop
    }

    @Test
    void testParseBatch() {
        when(threadManagerService.isParserExecutorRunning()).thenReturn(true);
        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getParserExecutor()).thenReturn(mockExecutorService);

        parserService.parseBatch();

        verify(mockExecutorService, times(1)).submit(any(Runnable.class));
    }
}
