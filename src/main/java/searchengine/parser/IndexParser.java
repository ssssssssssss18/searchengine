package searchengine.parser;

import searchengine.dto.IndexDto;
import searchengine.model.Site;

import java.util.List;

/**
 * Индексный парсер
 */
public interface IndexParser {

    /**
     * Запуск парсинга сайта
     *
     * @param site сайт для парсинга
     */
    void run(Site site);

    /**
     * Получение списка моделей поискогого индекса
     *
     * @return список моделей
     */
    List<IndexDto> getIndexDtoList();
}
