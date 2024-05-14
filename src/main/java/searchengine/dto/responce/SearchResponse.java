package searchengine.dto.responce;

import searchengine.dto.SearchDto;

import java.util.List;

/**
 *
 * @param result
 * @param count
 * @param data
 */
public record SearchResponse(boolean result, int count, List<SearchDto> data) {
}
