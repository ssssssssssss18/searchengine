package searchengine.dto;

/**
 * Поисковая модель
 *
 * @param siteUrl   ссылка на сайт
 * @param siteName  название сайта
 * @param uri       адрес страницы от корня сайта
 * @param title     заголовок сайта
 * @param snippet   фрагмент
 * @param relevance релевантность
 */
public record SearchDto(String siteUrl, String siteName, String uri, String title, String snippet, Float relevance) {
}
