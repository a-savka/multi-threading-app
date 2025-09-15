package ru.savka.demo.thread;

import ru.savka.demo.service.ThreadStateService;

public class WaitingThread implements Runnable {

    private final ThreadStateService threadStateService;
    private final Object monitor;

    public WaitingThread(ThreadStateService threadStateService, Object monitor) {
        this.threadStateService = threadStateService;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        threadStateService.waiting(monitor);
        System.out.println(Thread.currentThread().getName() + " finished.");
    }
}