package spider;

import java.util.LinkedList;

public class TaskManager {
    private LinkedList<String> tasks = new LinkedList<>();
    private int runningTasksCount = 0;
    private int limit;
    private int startedTasksCount = 0;

    public TaskManager(int limit) {
        this.limit = limit;
    }

    public synchronized void addTask(String url) {
        tasks.add(url);
    }

    public synchronized String getTask() {
        if (tasks.isEmpty() || startedTasksCount > limit) {
            return null;
        }
        runningTasksCount++;
        startedTasksCount++;
        try {
            Thread.sleep(100); // delay to avoid blocking on server
        } catch (InterruptedException e) {
        }
        return tasks.poll();
    }

    public synchronized boolean isTaskRunning() {
        return runningTasksCount > 0;
    }

    public synchronized void taskCompleted() {
        runningTasksCount--;
    }
}
