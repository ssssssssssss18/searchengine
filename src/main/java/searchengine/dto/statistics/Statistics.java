package searchengine.dto.statistics;

import java.util.List;

/**
 * Модель статистики
 *
 * @param total    модель общей статистики
 * @param detailed список деталей
 */
public record Statistics(Total total, List<Detailed> detailed) {
}
