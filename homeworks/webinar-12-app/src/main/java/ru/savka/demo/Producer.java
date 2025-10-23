package ru.savka.demo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {
    private final BlockingQueue<Event> queue;
    private final AtomicInteger eventCounter;

    public Producer(BlockingQueue<Event> queue, AtomicInteger eventCounter) {
        this.queue = queue;
        this.eventCounter = eventCounter;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int eventId = eventCounter.incrementAndGet();
                Event event = new Event(eventId, "Событие номер " + eventId);
                queue.put(event);
                System.out.println("Продюсер создал: " + event);
                Thread.sleep(1000); // Имитация времени на создание события
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Продюсер был прерван.");
        }
    }
}
