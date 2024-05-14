package searchengine.services;

import searchengine.dto.SearchDto;

import java.util.List;

/**
 * Сервис по поиску по сайтам
 */
public interface SearchService {

    /**
     * Поиск по всем сайтам
     *
     * @param searchText искомый текст
     * @param offset     смещение
     * @param limit      лимит
     * @return список поисковых моделей
     */
    List<SearchDto> searchAllSites(String searchText, int offset, int limit);

    /**
     * Поиск по сайту с url
     *
     * @param searchText искомый текст
     * @param url        ссылка на сайт
     * @param offset     смещение
     * @param limit      лимит
     * @return список поисковых моделей
     */
    List<SearchDto> siteSearch(String searchText, String url, int offset, int limit);
}
