package ru.savka.demo.thread;

import ru.savka.demo.service.ThreadStateService;

public class BlockedThread implements Runnable {

    private final ThreadStateService threadStateService;
    private final Object monitor;

    public BlockedThread(ThreadStateService threadStateService, Object monitor) {
        this.threadStateService = threadStateService;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        threadStateService.blocked(monitor);
        System.out.println(Thread.currentThread().getName() + " finished.");
    }
}