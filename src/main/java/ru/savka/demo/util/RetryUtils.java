package ru.savka.demo.util;

import java.util.concurrent.Callable;

public class RetryUtils {

    public static <T> T retry(Callable<T> task, int maxAttempts, long baseBackoffMs) throws Exception {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                long delay = baseBackoffMs * (1L << (attempt - 1));
                Thread.sleep(delay);
            }
        }
        throw new IllegalStateException("Should not reach here");
    }
}
