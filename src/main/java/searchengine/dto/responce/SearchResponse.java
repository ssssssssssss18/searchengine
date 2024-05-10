package searchengine.dto.responce;

import searchengine.dto.SearchDto;

import java.util.List;

public record SearchResponse(boolean result, int count, List<SearchDto> data) {
}
