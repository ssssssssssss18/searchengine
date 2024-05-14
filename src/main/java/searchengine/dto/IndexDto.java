package searchengine.dto;

/**
 * Модель поискогого индекса
 *
 * @param pageID  идентификатор страницы
 * @param lemmaID идентификатор леммы
 * @param rank    количество данной леммы для данной страницы
 */
public record IndexDto(Integer pageID, Integer lemmaID, Float rank) {
}
