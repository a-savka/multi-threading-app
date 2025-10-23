package ru.savka.demo;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable {
    private final BlockingQueue<Event> queue;

    public Consumer(BlockingQueue<Event> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Event event = queue.take();
                System.out.println("Консьюмер обработал: " + event);
                // Имитация времени на обработку
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Консьюмер был прерван.");
        }
    }
}
