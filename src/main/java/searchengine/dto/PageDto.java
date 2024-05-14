package searchengine.dto;

/**
 * Модель страницы
 *
 * @param url        ссылка на страницу
 * @param htmlCode   код страницы
 * @param statusCode статус-код страницы
 */
public record PageDto(String url, String htmlCode, int statusCode) {
}
