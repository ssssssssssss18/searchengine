package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.Detailed;
import searchengine.dto.statistics.Statistics;
import searchengine.dto.statistics.Total;
import searchengine.model.Site;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.StatisticsService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Override
    public Statistics getStatistics() {
        var total = getTotal();
        var detailed = getDetailedList();
        return new Statistics(total, detailed);
    }
    private Total getTotal() {
        var sites = siteRepository.count();
        var pages = pageRepository.count();
        var lemmas = lemmaRepository.count();
        return new Total(sites, pages, lemmas, true);
    }

    private Detailed getDetailed(Site site) {
        var url = site.getUrl();
        var name = site.getName();
        var status = site.getStatus();
        var statusTime = site.getStatusTime();
        var error = site.getLastError();
        var pages = pageRepository.countBySite(site);
        var lemmas = lemmaRepository.countBySite(site);
        return new Detailed(url, name, status, statusTime, error, pages, lemmas);
    }

    private List<Detailed> getDetailedList() {
        return siteRepository.findAll().stream()
                .map(this::getDetailed)
                .toList();
    }
}
