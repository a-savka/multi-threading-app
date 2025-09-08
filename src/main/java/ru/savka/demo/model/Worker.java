package ru.savka.demo.model;

import ru.savka.demo.dto.WorkerInfo;

public class Worker {

    private final WorkerInfo info;
    private final Thread worker;

    public Worker(WorkerInfo info, Thread worker) {
        this.info = info;
        this.worker = worker;
    }

    public WorkerInfo getIno() {
        return this.info;
    }

    public Thread getWorker() {
        return this.worker;
    }
}
