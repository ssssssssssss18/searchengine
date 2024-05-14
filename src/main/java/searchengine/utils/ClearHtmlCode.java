package searchengine.utils;

import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;

@UtilityClass
public class ClearHtmlCode {

    public static String clear(String content, String selector) {
        StringBuilder html = new StringBuilder();
        var doc = Jsoup.parse(content);
        var elements = doc.select(selector);

        doc.select(selector).forEach(element -> html.append(elements.html()));

        return Jsoup.parse(html.toString()).text();
    }
}
