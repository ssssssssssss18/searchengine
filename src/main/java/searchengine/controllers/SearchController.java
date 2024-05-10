package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.SearchDto;
import searchengine.dto.responce.FalseResponse;
import searchengine.dto.responce.SearchResponse;
import searchengine.repository.SiteRepository;
import searchengine.services.SearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;
    private final SiteRepository siteRepository;

    @GetMapping("/search")
    public ResponseEntity<Object> searchWords(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "site", required = false, defaultValue = "") String site,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {
        if (query.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FalseResponse(false, "Задан пустой поисковый запрос"));
        } else {
            List<SearchDto> searchData;
            if (!site.isEmpty()) {
                if (siteRepository.findByUrl(site) == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new FalseResponse(false, "Указанная страница не найдена"));
                } else {
                    searchData = searchService.siteSearch(query, site, offset, limit);
                }
            } else {
                searchData = searchService.allSiteSearch(query, offset, limit);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new SearchResponse(true, searchData.size(), searchData));
        }
    }
}
