package ru.savka.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CurrencyRateService {

    // Simulate fetching currency rate
    public double getRubRate() {
        try {
            // Simulate network delay
            TimeUnit.MILLISECONDS.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate a failure for a specific case
        if (System.currentTimeMillis() % 5 == 0) { // Fail randomly
            throw new RuntimeException("Ошибка получения курса валюты");
        }

        return 92.5; // Hardcoded rate
    }
}
