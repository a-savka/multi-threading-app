package ru.savka.webinar6app.service;

import java.util.concurrent.atomic.AtomicReference;

public class SingletonCache {
    private final AtomicReference<String> cache = new AtomicReference<>();

    public String getValue() {
        String value = cache.get();
        if (value == null) {
            String newValue = "This is the cached value";
            if (cache.compareAndSet(null, newValue)) {
                System.out.println("SingletonCache: Value was created and cached.");
                return newValue;
            }
        }
        System.out.println("SingletonCache: Value was retrieved from cache.");
        return cache.get();
    }
}
