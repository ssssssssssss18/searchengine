package searchengine.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.config.SiteConfig;
import searchengine.config.SiteConfigList;
import searchengine.dto.IndexDto;
import searchengine.dto.LemmaDto;
import searchengine.dto.PageDto;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@RequiredArgsConstructor
public class SiteIndex implements Runnable {
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final PageRepository pageRepository;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexParser indexParser;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final String url;
    private final SiteConfigList siteConfigList;

    @Override
    public void run() {
        if (siteRepository.findByUrl(url) != null) {
            log.info("Начато удаление данных - " + url);
            var site = siteRepository.findByUrl(url);
            siteRepository.delete(site);
        }
        log.info("Начата индексация - " + url);
        Site site = new Site();
        site.setUrl(url);
        site.setName(getName());
        site.setStatus(Status.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
        try {
            var pageDtoList = getPageDtoList();
            saveToBase(pageDtoList);
            getLemmasFromPages();
            indexingWords();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Индексация остановлена - " + url);
            site.setLastError("Индексация остановлена");
            site.setStatus(Status.FAILED);
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
        }
    }

    private List<PageDto> getPageDtoList() throws InterruptedException {
        if (!Thread.interrupted()) {
            String urlFormat = url + "/";
            List<PageDto> pageDtoVector = new Vector<>();
            List<String> urlList = new Vector<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(processorCoreCount);
            PageUrlParser pageUrlParser = new PageUrlParser(urlFormat, pageDtoVector, urlList);
            var pages = forkJoinPool.invoke(pageUrlParser);
            return new CopyOnWriteArrayList<>(pages);
        } else {
            throw new InterruptedException();
        }
    }

    private void getLemmasFromPages() throws InterruptedException {
        if (!Thread.interrupted()) {
            var site = siteRepository.findByUrl(url);
            log.info("сайт найден: " + site);
            site.setStatusTime(LocalDateTime.now());
            lemmaParser.run(site);
            List<LemmaDto> lemmaDtoList = new CopyOnWriteArrayList<>(lemmaParser.getLemmaDtoList());
            List<Lemma> lemmaList = new CopyOnWriteArrayList<>();
            lemmaDtoList.forEach(lemmaDto ->
                    lemmaList.add(new Lemma(lemmaDto.lemma(), lemmaDto.frequency(), site)));
            lemmaRepository.saveAll(lemmaList);
        } else {
            throw new InterruptedException();
        }
    }

    private void indexingWords() throws InterruptedException {
        if (!Thread.interrupted()) {
            var site = siteRepository.findByUrl(url);
            indexParser.run(site);
            List<IndexDto> indexDtoList = new CopyOnWriteArrayList<>(indexParser.getIndexList());
            List<Index> indexList = new CopyOnWriteArrayList<>();
            for (IndexDto indexDto : indexDtoList) {
                if (!Thread.interrupted()) {
                    var page = pageRepository.getReferenceById(indexDto.pageID());
                    var lemma = lemmaRepository.getReferenceById(indexDto.lemmaID());
                    site.setStatusTime(LocalDateTime.now());
                    indexList.add(new Index(page, lemma, indexDto.rank()));
                } else throw new InterruptedException();
            }
            indexRepository.saveAll(indexList);
            log.info("Индексация завершена - " + url);
            site.setStatusTime(LocalDateTime.now());
            site.setStatus(Status.INDEXED);
            siteRepository.save(site);
        } else {
            throw new InterruptedException();
        }
    }

    private void saveToBase(List<PageDto> pages) throws InterruptedException {
        if (!Thread.interrupted()) {
            List<Page> pageList = new CopyOnWriteArrayList<>();
            var site = siteRepository.findByUrl(url);
            for (PageDto page : pages) {
                var start = page.url().indexOf(url) + url.length();
                var pageFormat = page.url().substring(start);
                pageList.add(new Page(pageFormat, page.statusCode(), page.htmlCode(), site));
            }
            pageRepository.saveAll(pageList);
            log.info("список страниц сохранен");
        } else {
            log.error("не удалось сохранить список страниц");
            throw new InterruptedException();
        }
    }

    private String getName() {
        return siteConfigList.getSites().stream()
                .filter(site -> site.getUrl().equalsIgnoreCase(url))
                .map(SiteConfig::getName)
                .findFirst()
                .orElse("");
    }
}
