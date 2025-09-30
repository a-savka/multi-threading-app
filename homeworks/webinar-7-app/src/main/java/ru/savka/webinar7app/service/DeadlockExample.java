package ru.savka.webinar7app.service;

public class DeadlockExample {

    private final Object resourceA = new Object();
    private final Object resourceB = new Object();

    public void execute() {
        Thread thread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Поток 1: захватил ресурс A");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Поток 1: пытается захватить ресурс B");
                synchronized (resourceB) {
                    System.out.println("Поток 1: захватил ресурс B");
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println("Поток 2: захватил ресурс B");

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Поток 2: пытается захватить ресурс A");
                synchronized (resourceA) {
                    System.out.println("Поток 2: захватил ресурс A");
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}
