package ru.savka.demo.service;

public class ParserThread implements Runnable {

    @Override
    public void run() {
        // Mock implementation for parsing data
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
