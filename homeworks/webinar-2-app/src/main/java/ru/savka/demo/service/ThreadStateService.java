package ru.savka.demo.service;

import org.springframework.stereotype.Service;

@Service
public class ThreadStateService {

    public void timedWaiting() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void waiting(Object monitor) {
        synchronized (monitor) {
            try {
                monitor.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void blocked(Object monitor) {
        synchronized (monitor) {
            // This thread will be blocked if another thread holds the monitor
        }
    }
}
