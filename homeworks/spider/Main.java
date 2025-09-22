package spider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class Main {

//    private static final String siteUrl = "https://lenta.ru";
    private static final String siteUrl = "https://www.drom.ru";
    private static final int maxThreadCount = 10;
    private static final int maxPagesToParse = 300;
    private static final String outFilePath = "./sitemap.txt";

    public static void main(String[] args) {
        SiteMapState siteMapState = new SiteMapState(siteUrl);
        TaskManager manager = new TaskManager(maxPagesToParse);
        siteMapState.onUrlAdded = taskUrl ->  manager.addTask(taskUrl);
        manager.addTask(siteUrl);
        CountDownLatch latch = new CountDownLatch(maxThreadCount);
        for (int idx = 0; idx < maxThreadCount; idx++) {
            Spider spider = new Spider(idx, siteMapState, latch, manager);
            Thread thread = new Thread(spider);
            thread.start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        writeToFile(siteMapState.toString());
    }

    private static void writeToFile(String siteMap) {
        Path filePath = Paths.get(outFilePath);
        try {
            Files.write(filePath, Collections.singleton(siteMap));
            System.out.println("Result was written to " + outFilePath);
        } catch (IOException e) {
            System.out.println("Failed to write output to the file. " + e);
        }
    }

}
