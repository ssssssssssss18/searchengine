package searchengine.dto.responce;

import searchengine.dto.statistics.Statistics;

/**
 * Модель ответа со стастистикой
 *
 * @param result     принимает значение true
 * @param statistics статистика
 */
public record StatisticsResponse(boolean result, Statistics statistics) {
}
