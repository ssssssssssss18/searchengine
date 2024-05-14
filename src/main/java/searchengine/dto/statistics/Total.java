package searchengine.dto.statistics;

/**
 * Модель общей статистики
 *
 * @param sites      число сайтов
 * @param pages      число страниц
 * @param lemmas     кол-во лемм
 * @param isIndexing признак индексирования
 */
public record Total(Long sites, Long pages, Long lemmas, boolean isIndexing) {
}
