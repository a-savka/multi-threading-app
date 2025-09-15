package ru.savka.demo.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savka.demo.service.ThreadStateService;
import ru.savka.demo.thread.BlockedThread;
import ru.savka.demo.thread.TimedWaitingThread;
import ru.savka.demo.thread.WaitingThread;

@RestController
public class ThreadController {

    private final ThreadStateService threadStateService;
    private final Object monitor;

    public ThreadController(ThreadStateService threadStateService, @Qualifier("monitor") Object monitor) {
        this.threadStateService = threadStateService;
        this.monitor = monitor;
    }

    @GetMapping("/start")
    public String start() throws InterruptedException {
        System.out.println("--- Начало демонстрации состояний потоков ---");

        // 1. NEW
        Thread timedWaitingThread = new Thread(new TimedWaitingThread(threadStateService), "TimedWaitingThread");
        System.out.println(timedWaitingThread.getName() + " - State: " + timedWaitingThread.getState());

        Thread waitingThread = new Thread(new WaitingThread(threadStateService, monitor), "WaitingThread");
        System.out.println(waitingThread.getName() + " - State: " + waitingThread.getState());

        Thread blockedThread = new Thread(new BlockedThread(threadStateService, monitor), "BlockedThread");
        System.out.println(blockedThread.getName() + " - State: " + blockedThread.getState());

        // 2. RUNNABLE -> TIMED_WAITING
        timedWaitingThread.start();
        Thread.sleep(100); // Allow thread to start and enter sleep
        System.out.println(timedWaitingThread.getName() + " - State: " + timedWaitingThread.getState());


        // 3. RUNNABLE -> WAITING & BLOCKED
        synchronized (monitor) {
            waitingThread.start();
            Thread.sleep(100); // Allow thread to start and enter wait
            System.out.println(waitingThread.getName() + " - State: " + waitingThread.getState());

            blockedThread.start();
            Thread.sleep(100); // Allow thread to start and get blocked
            System.out.println(blockedThread.getName() + " - State: " + blockedThread.getState());

            monitor.notifyAll();
            System.out.println("Notified waiting threads.");
        }

        // 4. TERMINATED
        // Wait for threads to finish to show TERMINATED state
        Thread.sleep(1500);
        System.out.println(timedWaitingThread.getName() + " - State: " + timedWaitingThread.getState());
        System.out.println(waitingThread.getName() + " - State: " + waitingThread.getState());
        System.out.println(blockedThread.getName() + " - State: " + blockedThread.getState());


        System.out.println("--- Демонстрация завершена ---");
        return "Демонстрация состояний потоков завершена. Проверьте вывод в консоли.";
    }
}
