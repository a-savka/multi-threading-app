package ru.savka.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.repository.CurrencyRateRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import ru.savka.demo.worker.ParserWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private ExternalApiProperties externalApiProperties;
    @MockBean
    private ParserWorker parserWorker; // Mock the worker so we don't test its internals here

    private final AtomicBoolean isParserExecutorRunning = new AtomicBoolean(false);

    @BeforeEach
    void setUp() {
        // Reset state
        isParserExecutorRunning.set(false);

        // Configure deep stubs
        when(externalApiProperties.getFetch().getFetchIntervalMs()).thenReturn(100);
        when(externalApiProperties.getFetch().getApiRateLimitPerSecond()).thenReturn(10);

        // Mock ThreadManagerService with stateful behavior
        doAnswer(invocation -> {
            isParserExecutorRunning.set(true);
            return null;
        }).when(threadManagerService).startParserExecutor();

        doAnswer(invocation -> {
            isParserExecutorRunning.set(false);
            return null;
        }).when(threadManagerService).stopParserExecutor();

        when(threadManagerService.isParserExecutorRunning()).thenAnswer(invocation -> isParserExecutorRunning.get());

        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getParserExecutor()).thenReturn(mockExecutorService);
    }

    @Test
    void testStartParsing() {
        parserService.startParsing();
        verify(threadManagerService, times(1)).startParserExecutor();
        verify(loggingService, times(1)).logEvent("ParserService: Parsing started.");
        assertTrue(isParserExecutorRunning.get());
    }

    @Test
    void testStopParsing() {
        // Simulate running state by calling start first
        parserService.startParsing();
        assertTrue(isParserExecutorRunning.get());

        parserService.stopParsing();
        verify(threadManagerService, times(1)).stopParserExecutor();
        verify(loggingService, times(1)).logEvent("ParserService: Parsing stopped.");
        assertFalse(isParserExecutorRunning.get());
    }

    @Test
    void testParseBatch() {
        when(threadManagerService.isParserExecutorRunning()).thenReturn(true);
        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getParserExecutor()).thenReturn(mockExecutorService);

        parserService.parseBatch();

        verify(mockExecutorService, times(1)).submit(parserWorker);
    }
}
