package searchengine.services;

/**
 * Сервис индексации
 */
public interface IndexService {

    /**
     * Запуск индексации
     *
     * @return результат индексации
     */
    boolean startIndexing();

    /**
     * Остановка индексации
     *
     * @return результат остановки
     */
    boolean stopIndexing();

    /**
     * Запуск индексации по url
     *
     * @param url ссылка на страницу
     * @return результат индексации
     */
    boolean indexPage(String url);
}
