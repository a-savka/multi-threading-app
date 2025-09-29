package ru.savka.webinar6app.service;

import java.util.concurrent.atomic.AtomicReference;

public class SingletonCache {
    private final AtomicReference<String> cache = new AtomicReference<>();

    public String getValue() {
        String value = cache.get();
        if (value == null) {
            String newValue = "Это кэшированное значение";
            if (cache.compareAndSet(null, newValue)) {
                System.out.println("SingletonCache: Значение было создано и кэшировано.");
                return newValue;
            }
        }
        System.out.println("SingletonCache: Значение было получено из кэша.");
        return cache.get();
    }
}
