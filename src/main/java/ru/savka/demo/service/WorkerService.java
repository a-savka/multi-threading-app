package ru.savka.demo.service;

import org.springframework.stereotype.Service;
import ru.savka.demo.dto.WorkerInfo;
import ru.savka.demo.model.Worker;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    private final List<Worker> workers = new ArrayList<>();

    @PostConstruct
    public void init() {
        FetcherThread fetcherThread = new FetcherThread("currency-data-fetcher");
        workers.add(new Worker(
                new WorkerInfo(fetcherThread.getName(), LocalDateTime.now()),
                fetcherThread
        ));
        fetcherThread.start();

        Thread parserThread = new Thread(new ParserThread(), "currency-data-parser");
        workers.add(new Worker(
                new WorkerInfo(parserThread.getName(), LocalDateTime.now()),
                parserThread
        ));
        parserThread.start();
    }

    public List<WorkerInfo> getWorkers() {
        return workers.stream()
                .map(Worker::getIno)
                .collect(Collectors.toList());
    }
}
