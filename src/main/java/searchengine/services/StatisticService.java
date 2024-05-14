package searchengine.services;

import searchengine.dto.statistics.Statistics;

/**
 * Сервис получения общей статистики по сайтам
 */
public interface StatisticService {

    /**
     * Запрос общей статистики по запросу
     *
     * @return модель статистики
     */
    Statistics getStatistics();
}
