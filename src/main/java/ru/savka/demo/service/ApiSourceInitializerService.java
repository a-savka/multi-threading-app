package ru.savka.demo.service;

import ru.savka.demo.config.properties.ExternalApiProperties;
import ru.savka.demo.entity.ApiSourceConfig;
import ru.savka.demo.repository.ApiSourceConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ApiSourceInitializerService {

    private final ApiSourceConfigRepository apiSourceConfigRepository;
    private final ExternalApiProperties externalApiProperties;

    public ApiSourceInitializerService(ApiSourceConfigRepository apiSourceConfigRepository, ExternalApiProperties externalApiProperties) {
        this.apiSourceConfigRepository = apiSourceConfigRepository;
        this.externalApiProperties = externalApiProperties;
    }

    @Transactional
    public void initializeApiSources() {
        List<ExternalApiProperties.ApiSource> apiSources = externalApiProperties.getApiSources();
        for (ExternalApiProperties.ApiSource source : apiSources) {
            if (!apiSourceConfigRepository.existsById(source.getId())) {
                ApiSourceConfig config = new ApiSourceConfig(
                        source.getId(),
                        source.getName(),
                        source.getUrl(),
                        true,
                        source.getTimeoutMs(),
                        Instant.now()
                );
                apiSourceConfigRepository.save(config);
                System.out.println("Initialized API Source: " + source.getName());
            }
        }
    }
}
