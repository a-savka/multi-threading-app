package ru.savka.demo.thread;

import ru.savka.demo.service.ThreadStateService;

public class TimedWaitingThread implements Runnable {

    private final ThreadStateService threadStateService;

    public TimedWaitingThread(ThreadStateService threadStateService) {
        this.threadStateService = threadStateService;
    }

    @Override
    public void run() {
        threadStateService.timedWaiting();
        System.out.println(Thread.currentThread().getName() + " finished.");
    }
}