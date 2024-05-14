package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.Collection;
import java.util.List;

/**
 *
 */
@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    /**
     *
     * @param site
     * @return
     */
    List<Page> findBySite(Site site);

    /**
     *
     * @param site
     * @return
     */
    Long countBySite(Site site);

    /**
     *
     * @param lemmaList
     * @return
     */
    @Query(value = "SELECT p FROM Page p JOIN Index i ON p = i.page WHERE i.lemma IN :lemmas")
    List<Page> findByLemmaList(@Param("lemmas") Collection<Lemma> lemmaList);
}
