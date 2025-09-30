package ru.savka.webinar7app.service;

public class StarvationExample {

    private final Object sharedResource = new Object();

    public void execute() {
        Thread highPriorityThread = new Thread(() -> {
            synchronized (sharedResource) {
                System.out.println("Высокоприоритетный поток получил ресурс");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Высокоприоритетный поток освободил ресурс");
            }
        });

        Thread lowPriorityThread = new Thread(() -> {
            synchronized (sharedResource) {
                System.out.println("Низкоприоритетный поток получил ресурс");
            }
        });

        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);

        lowPriorityThread.start();
        highPriorityThread.start();
    }
}
