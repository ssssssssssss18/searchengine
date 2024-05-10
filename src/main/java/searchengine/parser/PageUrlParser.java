package searchengine.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import searchengine.dto.PageDto;
import searchengine.utils.RandomUserAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@Slf4j
public class PageUrlParser extends RecursiveTask<List<PageDto>> {
    private final String url;
    private final List<String> urlList;
    private final List<PageDto> pageDtoList;
    private static final Set<String> EXCLUDED_EXTENSIONS = Set.of(".pdf", ".jpg", ".JPG", ".png", "#");

    public PageUrlParser(String url, List<PageDto> pageDtoList, List<String> urlList) {
        this.url = url;
        this.pageDtoList = pageDtoList;
        this.urlList = urlList;
    }

    @Override
    protected List<PageDto> compute() {
        try {
            Thread.sleep(150);
            var doc = getConnect(url);
            var html = doc.outerHtml();
            var response = doc.connection().response();
            var status = response.statusCode();

            PageDto pageDto = new PageDto(url, html, status);
            pageDtoList.add(pageDto);

            var elements = doc.select("a");
            List<PageUrlParser> taskList = new ArrayList<>();
            for (Element element : elements) {
                var link = element.attr("abs:href");
                if (checkLink(element, link)) {
                    urlList.add(link);
                    PageUrlParser task = new PageUrlParser(link, pageDtoList, urlList);
                    taskList.add(task);
                    task.fork();
                    log.info("URL обработан: " + link);
                }
            }
            taskList.forEach(ForkJoinTask::join);
        } catch (Exception e) {
            log.debug("Ошибка парсинга - " + url);
            PageDto pageDto = new PageDto(url, "", 500);
            pageDtoList.add(pageDto);
        }
        return pageDtoList;
    }

    private boolean checkLink(Element element, String link) {
        var baseUri = element.baseUri();
        return link.startsWith(baseUri)
                && !link.equals(baseUri)
                && EXCLUDED_EXTENSIONS.stream().noneMatch(link::contains)
                && !urlList.contains(link);
    }

    private Document getConnect(String url) {
        Document doc = null;
        try {
            Thread.sleep(150);
            doc = Jsoup.connect(url)
                    .userAgent(RandomUserAgent.getRandomUserAgent())
                    .referrer("http://www.google.com")
                    .get();
        } catch (Exception e) {
            log.debug("Не удалось установить подключение с " + url);
        }
        return doc;
    }

}
