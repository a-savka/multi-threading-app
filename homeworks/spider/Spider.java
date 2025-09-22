package spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class Spider implements Runnable {

    private final int id;
    private final SiteMapState siteMapState;
    private final CountDownLatch latch;
    private final TaskManager taskManager;

    public Spider(
            int id,
            SiteMapState siteMapState,
            CountDownLatch latch,
            TaskManager taskManager
    ) {
        this.id = id;
        this.siteMapState = siteMapState;
        this.latch = latch;
        this.taskManager = taskManager;
    }

    public void run() {
        while (true) {
            String url = taskManager.getTask();
            if (url == null) {
                if (!taskManager.isTaskRunning()) {
                    break;
                }
            } else {
//                processUrl(url);
                processUrlWithJsoup(url);
                taskManager.taskCompleted();
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Spider #" + id + " finished");
        latch.countDown();
    }

    private void processUrl(String url) {
        PageParser parser = new PageParser(url);
        System.out.println("Spider #" + id + " started processing: " + url);
        try {
            URL pageUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) pageUrl.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                parser.parseLinks(line);
                for (String link : parser.parseLinks(line)) {
                    siteMapState.addChildPage(url, link);
                }
            }
        } catch (IOException e) {
            System.out.println("Error!" + e);
        }
    }

    private void processUrlWithJsoup(String url) {
        PageParser parser = new PageParser(url);
        System.out.println("Spider #" + id + " started processing: " + url);
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a");
            for (Element link : links) {
                String linkUrl = link.absUrl("href");
                if (linkUrl.indexOf("#") == -1 && parser.isSameDomain(linkUrl) && parser.isSubPage(linkUrl)) {
                    siteMapState.addChildPage(url, linkUrl);
                }
            }
        } catch (IOException e) {
            System.out.println("Error!" + e);
        }
    }

}
