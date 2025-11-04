package ru.savka.demo.controller;

import ru.savka.demo.dto.RateDto;
import ru.savka.demo.entity.CurrencyRate;
import ru.savka.demo.entity.RawApiResponse;
import ru.savka.demo.entity.RawApiResponse.Status;
import ru.savka.demo.repository.CurrencyRateRepository;
import ru.savka.demo.repository.RawApiResponseRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rates")
public class RateController {

    private final CurrencyRateRepository currencyRateRepository;
    private final RawApiResponseRepository rawApiResponseRepository;

    public RateController(CurrencyRateRepository currencyRateRepository, RawApiResponseRepository rawApiResponseRepository) {
        this.currencyRateRepository = currencyRateRepository;
        this.rawApiResponseRepository = rawApiResponseRepository;
    }

    @GetMapping("/latest")
    public ResponseEntity<RateDto> getLatestRate(@RequestParam String currency) {
        Optional<CurrencyRate> latestRate = currencyRateRepository.findTopByCurrencyOrderByRateDateDesc(currency);
        return latestRate.map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RateDto>> getRatesByCurrencyAndDateRange(
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<CurrencyRate> rates = currencyRateRepository.findByCurrencyAndRateDateBetween(currency, from, to);
        List<RateDto> rateDtos = rates.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rateDtos);
    }

    @GetMapping("/raw/responses")
    public ResponseEntity<List<RawApiResponse>> getRawResponsesByStatus(@RequestParam Status status) {
        // Using Pageable.unpaged() for simplicity, can be extended with pagination
        List<RawApiResponse> rawResponses = rawApiResponseRepository.findByStatusOrderByReceivedAtAsc(status, org.springframework.data.domain.Pageable.unpaged());
        return ResponseEntity.ok(rawResponses);
    }

    private RateDto convertToDto(CurrencyRate currencyRate) {
        return new RateDto(
                currencyRate.getCurrency(),
                currencyRate.getRate(),
                currencyRate.getRateDate(),
                currencyRate.getSourceId()
        );
    }
}
