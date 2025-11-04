package ru.savka.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FetchJobStatusDto {
    private boolean isFetchRunning;
    private int fetchActiveCount;
    private int parserActiveCount;
    private int queuedFetches;
    private int queuedParsers;
    private String status; // e.g., "RUNNING", "STOPPED"

    // Explicit AllArgsConstructor
    public FetchJobStatusDto(boolean isFetchRunning, int fetchActiveCount, int parserActiveCount, int queuedFetches, int queuedParsers, String status) {
        this.isFetchRunning = isFetchRunning;
        this.fetchActiveCount = fetchActiveCount;
        this.parserActiveCount = parserActiveCount;
        this.queuedFetches = queuedFetches;
        this.queuedParsers = queuedParsers;
        this.status = status;
    }
}
