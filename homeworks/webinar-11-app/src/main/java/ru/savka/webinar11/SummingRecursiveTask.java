package ru.savka.webinar11;

import java.util.List;
import java.util.concurrent.RecursiveTask;

public class SummingRecursiveTask extends RecursiveTask<Long> {

    private final List<Integer> numbers;
    private final int threshold;

    public SummingRecursiveTask(List<Integer> numbers, int threshold) {
        this.numbers = numbers;
        this.threshold = threshold;
    }

    @Override
    protected Long compute() {
        if (numbers.size() <= threshold) {
            return sumSequentially();
        } else {
            int middle = numbers.size() / 2;
            SummingRecursiveTask leftTask = new SummingRecursiveTask(numbers.subList(0, middle), threshold);
            SummingRecursiveTask rightTask = new SummingRecursiveTask(numbers.subList(middle, numbers.size()), threshold);

            leftTask.fork();
            Long rightResult = rightTask.compute();
            Long leftResult = leftTask.join();

            return leftResult + rightResult;
        }
    }

    private Long sumSequentially() {
        long sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        System.out.println(Thread.currentThread().getName() + ": Суммирование " + numbers.size() + " элементов.");
        return sum;
    }
}
