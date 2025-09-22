package spider.paralleltest;

public class TestRunner extends Thread {

    private static int runningCount = 0;
    private final Object waitObject;
    private final int id;

    public TestRunner(int id, Object waitObject) {
        this.id = id;
        this.waitObject = waitObject;
    }

    @Override
    public void run() {
        try {
         synchronized (waitObject) {
             while (runningCount >= 3) {
                 waitObject.wait();
             }
             runningCount++;
             System.out.println("Starting job for thread " + id);
             Thread.sleep(2000);

         }
        } catch (InterruptedException e) {
        }
        System.out.println("Finished job for thread " + id);
        runningCount--;
    }

}
