package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.entity.ApiSourceConfig;
import ru.savka.demo.repository.ApiSourceConfigRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @MockBean
    private ExternalApiProperties externalApiProperties;

    private ExternalApiProperties.Fetch mockFetchProperties;
    private ExternalApiProperties.ApiSource mockApiSource1;
    private ExternalApiProperties.ApiSource mockApiSource2;
    private ApiSourceConfig mockApiSourceConfig1;
    private ApiSourceConfig mockApiSourceConfig2;

    @BeforeEach
    void setUp() {
        // Mock ExternalApiProperties
        mockFetchProperties = new ExternalApiProperties.Fetch();
        mockFetchProperties.setFetchIntervalMs(100); // Short interval for testing
        ExternalApiProperties.Retry mockRetry = new ExternalApiProperties.Retry();
        mockRetry.setMaxAttempts(3);
        mockRetry.setBaseBackoffMs(10);
        mockFetchProperties.setRetry(mockRetry);

        when(externalApiProperties.getFetch()).thenReturn(mockFetchProperties);

        // Mock ApiSourceConfig
        mockApiSource1 = new ExternalApiProperties.ApiSource();
        mockApiSource1.setId("exchanger1");
        mockApiSource1.setName("ExampleRates1");
        mockApiSource1.setUrl("https://api.example.com/latest?base=USD");
        mockApiSource1.setTimeoutMs(5000);

        mockApiSource2 = new ExternalApiProperties.ApiSource();
        mockApiSource2.setId("exchanger2");
        mockApiSource2.setName("AnotherSource");
        mockApiSource2.setUrl("https://api.other.com/rates");
        mockApiSource2.setTimeoutMs(7000);

        mockApiSourceConfig1 = new ApiSourceConfig("exchanger1", "ExampleRates1", "https://api.example.com/latest?base=USD", true, 5000, Instant.now());
        mockApiSourceConfig2 = new ApiSourceConfig("exchanger2", "AnotherSource", "https://api.other.com/rates", true, 7000, Instant.now());

        List<ApiSourceConfig> allSources = Arrays.asList(mockApiSourceConfig1, mockApiSourceConfig2);
        when(apiSourceConfigRepository.findAll()).thenReturn(allSources);
        when(apiSourceConfigRepository.findById("exchanger1")).thenReturn(java.util.Optional.of(mockApiSourceConfig1));

        // Mock ThreadManagerService
        when(threadManagerService.isFetchExecutorRunning()).thenReturn(false); // Initially not running
        ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
        when(threadManagerService.getFetchExecutor()).thenReturn(mockExecutorService);
    }

    @Test
    void testStartFetching() {
        fetchService.startFetching();
        verify(threadManagerService, times(1)).startFetchExecutor();
        verify(loggingService, times(1)).logEvent("FetchService: Fetching started.");
        assertTrue(threadManagerService.isFetchExecutorRunning()); // Should be true after start
    }

    @Test
    void testStopFetching() {
        // Simulate running state
        when(threadManagerService.isFetchExecutorRunning()).thenReturn(true);
        fetchService.stopFetching();
        verify(threadManagerService, times(1)).stopFetchExecutor();
        verify(loggingService, times(1)).logEvent("FetchService: Fetching stopped.");
        assertFalse(threadManagerService.isFetchExecutorRunning()); // Should be false after stop
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
