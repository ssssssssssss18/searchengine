package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.parser.IndexParser;
import searchengine.parser.LemmaParser;
import searchengine.parser.SiteIndex;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.IndexService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private final SitesList sitesList;
    private final SiteRepository siteRepository;
    private ExecutorService executorService;
    private final PageRepository pageRepository;
    private final LemmaParser lemmaParser;
    private final LemmaRepository lemmaRepository;
    private final IndexParser indexParser;
    private final IndexRepository indexRepository;

    @Override
    public boolean startIndexing() {
        if (isIndexingActive()) {
            return false;
        } else {
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            sitesList.getSites().forEach(site -> executorService.submit(createSiteIndexInstance(site.getUrl())));
            executorService.shutdown();
            return true;
        }
    }

    @Override
    public boolean stopIndexing() {
        if (isIndexingActive()) {
            log.info("Останавливаем индексацию");
            executorService.shutdownNow();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean indexPage(String url) {
        if (urlCheck(url)) {
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            executorService.submit(createSiteIndexInstance(url));
            executorService.shutdown();
            return true;
        } else {
            return false;
        }
    }

    private SiteIndex createSiteIndexInstance(String url) {
        return new SiteIndex(
                pageRepository,
                lemmaParser,
                lemmaRepository,
                indexParser,
                indexRepository,
                siteRepository,
                url,
                sitesList);
    }

    private boolean isIndexingActive() {
        return siteRepository.findAll().stream()
                .anyMatch(site -> site.getStatus() == Status.INDEXING);
    }

    private boolean urlCheck(String url) {
        return sitesList.getSites().stream()
                .anyMatch(site -> site.getUrl().equalsIgnoreCase(url));
    }
}
