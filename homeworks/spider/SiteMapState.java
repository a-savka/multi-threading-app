package spider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SiteMapState {
    final private WebPage rootPage;
    final private Map<String, WebPage> flatMap = new HashMap<>();
    public Consumer<String> onUrlAdded;

    public SiteMapState(String rootUrl) {
        rootPage = new WebPage(rootUrl, 0);
        flatMap.put(rootUrl, rootPage);
    }

    public synchronized void addChildPage(String parentUrl, String pageUrl) {
        if (!flatMap.containsKey(pageUrl) && flatMap.containsKey(parentUrl)) {
            flatMap.put(pageUrl, flatMap.get(parentUrl).addChild(pageUrl));
            if (onUrlAdded != null) {
                onUrlAdded.accept(pageUrl);
            }
        }
    }

    @Override
    public String toString() {
        return rootPage.toString();
    }
}
