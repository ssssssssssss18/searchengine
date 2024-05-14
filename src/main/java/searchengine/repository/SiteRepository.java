package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

/**
 * Репозиторий сайта
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    /**
     * Получение сайта по url
     *
     * @param url ссылка на сайт
     * @return сайт
     */
    Site getSiteByUrl(String url);
}
