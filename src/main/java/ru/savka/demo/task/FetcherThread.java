package ru.savka.demo.task;

public class FetcherThread extends Thread {

    public FetcherThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        // Mock implementation for fetching data
        while (true) {
            try {
                Thread.sleep(10000); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
