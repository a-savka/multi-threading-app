package spider;

import java.util.ArrayList;
import java.util.List;

public class WebPage {
    final private String url;
    final private int level;
    final private List<WebPage> children;

    public WebPage(String url, int level) {
        this.url = url;
        this.level = level;
        this.children = new ArrayList<>();
    }

    public WebPage addChild(String url) {
        WebPage newPage = new WebPage(url, level  +1);
        children.add(newPage);
        return newPage;
    }

    @Override
    public String toString() {
        return "\t".repeat(level) +
                this.url + '\n' +
                (children.isEmpty() ?
                        "" :
                        children.stream().map(child -> child.toString()).reduce((prev, curr) -> prev + curr).get());
    }
}
