package spider.paralleltest;

public class Main {
    public static void main(String[] args) {
        Object waitObject = new Object();
        for (int i = 0; i < 10; i++) {
            TestRunner runner = new TestRunner(i, waitObject);
            runner.start();
        }
        System.out.println("All was run");
    }
}
