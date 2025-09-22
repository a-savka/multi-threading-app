package spider;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageParser {
    final String pageUrl;
    final String baseUrl;
    final String domainUrl;
    public PageParser(String pageUrl) {
        this.pageUrl = pageUrl;
        this.baseUrl = pageUrl.replaceAll("\\?.*$", "");
        this.domainUrl = baseUrl.replaceAll("(https?://[^/]+).*$", "$1");
    }

    public Set<String> parseLinks(String line) {
        String[] regexes = {
                "<a [^>]*href=\"([^\"]+)\" ",
                "<a [^>]*href='([^\"]+)' ",
                "<a [^>]*href=[^'\"]([^\"]+) "
        };
        Set<String> result = new HashSet<>();
        for (String regex : regexes) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String url = toAbsoluteUrl(matcher.group(1));
                if (url.indexOf("#") == -1 && isSameDomain(url) && isSubPage(url)) {
                    result.add(url);
                }
            }
        }
        return result;
    }

    private String toAbsoluteUrl(String url) {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            return url;
        } else if (url.startsWith("//")) {
            return "https:" + url;
        } else if (url.startsWith("/")) {
            return domainUrl + url;
        }
        return baseUrl + (baseUrl.endsWith("/") ? url : ("/" + url));
    }

    public boolean isSameDomain(String link) {
        return link.startsWith(domainUrl);
    }

    public boolean isSubPage(String link) {
        return link.startsWith(baseUrl);
    }

}
