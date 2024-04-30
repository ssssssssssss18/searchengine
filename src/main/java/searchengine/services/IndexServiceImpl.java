package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.repository.SiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {

    private final SitesList sitesList;
    private final SiteRepository siteRepository;

    @Override
    public boolean startIndexing() {
        if (!checkIndexing()) {
            return false;
        }
        sitesList.getSites().forEach(System.out::println);
        return true;
    }

    private boolean checkIndexing() {
        return siteRepository.findAll().stream()
                .anyMatch(site -> site.getStatus() == Status.INDEXING);
    }

    @Override
    public boolean stopIndexing() {
        return checkIndexing();
    }

    @Override
    public void indexPage() {

    }
}
