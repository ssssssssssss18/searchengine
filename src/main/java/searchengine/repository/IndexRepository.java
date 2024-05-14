package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;

/**
 * Репозиторий индексов слов
 */
@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    /**
     * Поиск индексов слов по страницам и леммам
     *
     * @param lemmaList список лемм
     * @param pageList  список страниц
     * @return список индексов
     */
    @Query(value = "SELECT i FROM Index i WHERE i.lemma IN :lemmas AND i.page IN :pages")
    List<Index> findByPagesAndLemmas(@Param("lemmas") List<Lemma> lemmaList, @Param("pages") List<Page> pageList);
}
