package ru.savka.webinar7app.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LivelockExample {

    private final Lock lock1 = new ReentrantLock(true);
    private final Lock lock2 = new ReentrantLock(true);

    public void execute() {
        new Thread(() -> {
            while (true) {
                try {
                    if (lock1.tryLock(50, TimeUnit.MILLISECONDS)) {
                        System.out.println("Поток 1: захватил блокировку 1");
                        if (lock2.tryLock(50, TimeUnit.MILLISECONDS)) {
                            System.out.println("Поток 1: захватил блокировку 2");
                            lock2.unlock();
                            lock1.unlock();
                            break;
                        } else {
                            System.out.println("Поток 1: не смог захватить блокировку 2, освобождаю блокировку 1");
                            lock1.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    if (lock2.tryLock(50, TimeUnit.MILLISECONDS)) {
                        System.out.println("Поток 2: захватил блокировку 2");
                        if (lock1.tryLock(50, TimeUnit.MILLISECONDS)) {
                            System.out.println("Поток 2: захватил блокировку 1");
                            lock1.unlock();
                            lock2.unlock();
                            break;
                        } else {
                            System.out.println("Поток 2: не смог захватить блокировку 1, освобождаю блокировку 2");
                            lock2.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
