package ru.savka.webinar11;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
public class ForkJoinService {

    private final ForkJoinPool forkJoinPool;

    public ForkJoinService() {
        // Создаем ForkJoinPool с количеством потоков, равным количеству доступных процессоров
        this.forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    public long calculateSum(List<Integer> numbers, int threshold) {
        SummingRecursiveTask task = new SummingRecursiveTask(numbers, threshold);
        return forkJoinPool.invoke(task);
    }

    public void shutdown() {
        forkJoinPool.shutdown();
    }
}
