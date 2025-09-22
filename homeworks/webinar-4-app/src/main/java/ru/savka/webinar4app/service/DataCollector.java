package ru.savka.webinar4app.service;

import ru.savka.webinar4app.model.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataCollector {
    private long processedCount = 0;
    private final List<Item> collectedItems = new ArrayList<>();
    private final Set<String> processedKeys = new HashSet<>();

    public synchronized void collectItem(Item item) {
        if (!isAlreadyProcessed(item.getKey())) {
            collectedItems.add(item);
            processedKeys.add(item.getKey());
        }
    }

    public synchronized void incrementProcessed() {
        processedCount++;
    }

    public synchronized boolean isAlreadyProcessed(String key) {
        return processedKeys.contains(key);
    }

    public synchronized long getProcessedCount() {
        return processedCount;
    }

    public synchronized List<Item> getCollectedItems() {
        return new ArrayList<>(collectedItems);
    }
}
