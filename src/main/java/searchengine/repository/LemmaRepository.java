package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;

/**
 * Репозиторий лемм
 */
@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

    /**
     * Поиск лемм по сайту
     *
     * @param site сайт
     * @return список лемм
     */
    List<Lemma> findLemmasBySite(Site site);

    /**
     * Получение кол-ва лемм для сайта
     *
     * @param site сайт
     * @return число лемм
     */
    Long countLemmasBySite(Site site);

    /**
     * Поиск лемм по списку заданных лемм для указанного сайта
     *
     * @param lemmaList список лемм
     * @param site      сайт
     * @return список лемм
     */
    @Query(value = "SELECT l FROM Lemma l WHERE l.lemma IN :lemmas AND l.site = :site")
    List<Lemma> findLemmaListBySite(@Param("lemmas") List<String> lemmaList,
                                    @Param("site") Site site);
}
