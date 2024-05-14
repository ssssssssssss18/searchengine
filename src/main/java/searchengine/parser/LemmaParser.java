package searchengine.parser;

import searchengine.dto.LemmaDto;
import searchengine.model.Site;

import java.util.List;

/**
 * Парсер лемм
 */
public interface LemmaParser {

    /**
     * Запуск парсинга сайта
     *
     * @param site сайт для парсинга
     */
    void run(Site site);

    /**
     * Получение списка моделей лемм
     *
     * @return список моделей
     */
    List<LemmaDto> getLemmaDtoList();
}
