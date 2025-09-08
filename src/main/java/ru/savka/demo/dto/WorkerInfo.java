package ru.savka.demo.dto;

import java.time.LocalDateTime;

public class WorkerInfo {
    private String name;
    private LocalDateTime startTime;

    public WorkerInfo(String name, LocalDateTime startTime) {
        this.name = name;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
