package ru.savka.webinar6app.service;

public class VolatileExample {
    private volatile boolean running = true;

    public void start() {
        new Thread(() -> {
            while (running) {
                try {
                    System.out.println("VolatileExample: Still running...");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("VolatileExample: Stopped.");
        }).start();
    }

    public void stop() {
        running = false;
    }
}
