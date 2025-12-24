package ru.savka.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.entity.ApiSourceConfig;
import ru.savka.demo.repository.ApiSourceConfigRepository;
import ru.savka.demo.repository.RawApiResponseRepository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class FetchServiceTest {

    @Autowired
    private FetchService fetchService;

    @MockBean
    private ThreadManagerService threadManagerService;
    @MockBean
    private ApiSourceConfigRepository apiSourceConfigRepository;
    @MockBean
    private ApiClientService apiClientService;
    @MockBean
    private RawApiResponseRepository rawApiResponseRepository;
    @MockBean
    private LoggingService loggingService;
    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private ExternalApiProperties externalApiProperties;

    private final AtomicBoolean isFetchExecutorRunning = new AtomicBoolean(false);

    @BeforeEach
    void setUp() {
        // Reset state before each test
        isFetchExecutorRunning.set(false);

        // Configure deep stubs
        when(externalApiProperties.getFetch().getFetchIntervalMs()).thenReturn(100);
        when(externalApiProperties.getFetch().getApiRateLimitPerSecond()).thenReturn(10);


        // Mock ApiSourceConfig
        ApiSourceConfig mockApiSourceConfig1 = new ApiSourceConfig("exchanger1", "ExampleRates1", "https://api.example.com/latest?base=USD", true, 5000, Instant.now());
        ApiSourceConfig mockApiSourceConfig2 = new ApiSourceConfig("exchanger2", "AnotherSource", "https://api.other.com/rates", true, 7000, Instant.now());
        List<ApiSourceConfig> allSources = Arrays.asList(mockApiSourceConfig1, mockApiSourceConfig2);
        when(apiSourceConfigRepository.findAll()).thenReturn(allSources);
        when(apiSourceConfigRepository.findById("exchanger1")).thenReturn(java.util.Optional.of(mockApiSourceConfig1));

        // Mock ThreadManagerService with stateful behavior
        doAnswer(invocation -> {
            isFetchExecutorRunning.set(true);
            return null;
        }).when(threadManagerService).startFetchExecutor();

        doAnswer(invocation -> {
            isFetchExecutorRunning.set(false);
            return null;
        }).when(threadManagerService).stopFetchExecutor();

        when(threadManagerService.isFetchExecutorRunning()).thenAnswer(invocation -> isFetchExecutorRunning.get());

        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getFetchExecutor()).thenReturn(mockExecutorService);
    }

    @Test
    void testStartFetching() {
        fetchService.startFetching();
        verify(threadManagerService, times(1)).startFetchExecutor();
        verify(loggingService, times(1)).logEvent("FetchService: Fetching started.");
        assertTrue(isFetchExecutorRunning.get());
    }

    @Test
    void testStopFetching() {
        // Simulate running state
        isFetchExecutorRunning.set(true);
        fetchService.stopFetching();
        verify(threadManagerService, times(1)).stopFetchExecutor();
        verify(loggingService, times(1)).logEvent("FetchService: Fetching stopped.");
        assertFalse(isFetchExecutorRunning.get());
    }

    @Test
    void testSubmitImmediateFetch() {
        when(threadManagerService.isFetchExecutorRunning()).thenReturn(true);
        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getFetchExecutor()).thenReturn(mockExecutorService);

        fetchService.submitImmediateFetch("exchanger1");

        verify(apiSourceConfigRepository, times(1)).findById("exchanger1");
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockExecutorService, times(1)).submit(runnableCaptor.capture());
        // You could further assert properties of the captured runnable if needed
    }
}
