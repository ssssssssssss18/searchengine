package searchengine.dto.statistics;

import searchengine.model.Status;

import java.time.LocalDateTime;

/**
 * Детали статистики
 *
 * @param url        ссылка на сайт
 * @param name       название сайта
 * @param status     статус-код
 * @param statusTime дата и время статуса
 * @param error      наличие ошибки
 * @param pages      число страниц
 * @param lemmas     кол-во лемм
 */
public record Detailed(String url, String name, Status status, LocalDateTime statusTime, String error, Long pages,
                       Long lemmas) {
}
