package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfigList;
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
    private final SiteConfigList siteConfigList;
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
            log.info("Начинаем индексацию");
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            siteConfigList.getSites().forEach(site -> executorService.submit(createSiteIndexInstance(site.getUrl())));
            executorService.shutdown();
            return true;
        }
    }

    @Override
    public boolean stopIndexing() {
        if (isIndexingActive()) {
            log.info("Останавливаем индексацию");
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            executorService.shutdownNow();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean indexPage(String url) {
        if (StringUtils.isNotEmpty(url) && urlCheck(url)) {
            log.info("Начинаем индексацию по url: {}", url);
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            executorService.submit(createSiteIndexInstance(url));
            executorService.shutdown();
            return true;
        } else {
            log.error("Пустая ссылка или ссылка не прошла проверку: {}", url);
            return false;
        }
    }

    private SiteIndex createSiteIndexInstance(String url) {
        return new SiteIndex(
                pageRepository, lemmaParser, lemmaRepository, indexParser,
                indexRepository, siteRepository, url, siteConfigList
        );
    }

    private boolean isIndexingActive() {
        return siteRepository.findAll().stream()
                .anyMatch(site -> site.getStatus() == Status.INDEXING);
    }

    private boolean urlCheck(String url) {
        return siteConfigList.getSites().stream()
                .anyMatch(site -> site.getUrl().equalsIgnoreCase(url));
    }
}
