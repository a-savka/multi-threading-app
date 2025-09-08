package ru.savka.demo.service;

import org.springframework.stereotype.Service;
import ru.savka.demo.dto.WorkerInfo;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerService {

    private final List<WorkerInfo> workers = new ArrayList<>();

    @PostConstruct
    public void init() {
        FetcherThread fetcherThread = new FetcherThread("currency-data-fetcher");
        workers.add(new WorkerInfo(fetcherThread.getName(), LocalDateTime.now()));
        fetcherThread.start();

        Thread parserThread = new Thread(new ParserThread(), "currency-data-parser");
        workers.add(new WorkerInfo(parserThread.getName(), LocalDateTime.now()));
        parserThread.start();
    }

    public List<WorkerInfo> getWorkers() {
        return workers;
    }
}
