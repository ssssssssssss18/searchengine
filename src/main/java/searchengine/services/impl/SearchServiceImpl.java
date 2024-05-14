package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import searchengine.dto.SearchDto;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.morphology.Morphology;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.SearchService;
import searchengine.utils.ClearHtmlCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final Morphology morphology;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;

    @Value("${title-selector}")
    private String TITLE_SELECTOR;
    @Value("${body-selector}")
    private String BODY_SELECTOR;

    @Override
    public List<SearchDto> searchAllSites(String searchText, int offset, int limit) {
        log.info("Получаем инфомацию по поиску \"" + searchText + "\"");
        var siteList = siteRepository.findAll();
        List<SearchDto> result = new ArrayList<>();
        List<Lemma> foundLemmaList = new ArrayList<>();
        var textLemmaList = getLemmaFromText(searchText);
        for (Site site : siteList) {
            foundLemmaList.addAll(getLemmaListFromSite(textLemmaList, site));
        }
        List<SearchDto> searchData = new ArrayList<>(getSearchDtoList(foundLemmaList, textLemmaList, offset, limit));
        searchData.sort((o1, o2) -> Float.compare(o2.relevance(), o1.relevance()));
        if (searchData.size() > limit) {
            for (int i = offset; i < limit; i++) {
                result.add(searchData.get(i));
            }
            return result;
        }
        return searchData;
    }

    @Override
    public List<SearchDto> siteSearch(String searchText, String url, int offset, int limit) {
        log.info("Получаем информацию по поиску \"" + searchText + "\" с сайта - " + url);
        var site = siteRepository.getSiteByUrl(url);

        var textLemmaList = getLemmaFromText(searchText);
        var foundLemmaList = getLemmaListFromSite(textLemmaList, site);

        return getSearchDtoList(foundLemmaList, textLemmaList, offset, limit);
    }

    private List<SearchDto> getSearchDtoList(List<Lemma> lemmaList, List<String> textLemmaList, int offset, int limit) {
        List<SearchDto> result = new ArrayList<>();
        if (lemmaList.size() >= textLemmaList.size()) {
            var foundPageList = pageRepository.findByLemmaList(lemmaList);
            var foundIndexList = indexRepository.findByPagesAndLemmas(lemmaList, foundPageList);
            var sortedPageByAbsRelevance = getPageAbsRelevance(foundPageList, foundIndexList);
            var dataList = getSearchData(sortedPageByAbsRelevance, textLemmaList);
            if (offset > dataList.size()) {
                return new ArrayList<>();
            }
            if (dataList.size() > limit) {
                for (int i = offset; i < limit; i++) {
                    result.add(dataList.get(i));
                }
                return result;
            } else {
                return dataList;
            }
        } else {
            return result;
        }
    }

    private String getSnippet(String content, List<String> lemmaList) {
        var lemmaIndex = lemmaList.stream()
                .flatMap(lemma -> morphology.findLemmaIndexInText(content, lemma).stream())
                .sorted()
                .toList();

        var wordsList = getWordsFromContent(content, lemmaIndex);

        return wordsList.stream()
                .limit(6) // Ограничиваем количество слов до 6
                .collect(Collectors.joining("... "));
    }

    private List<String> getWordsFromContent(String content, List<Integer> lemmaIndex) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < lemmaIndex.size(); i++) {
            int start = lemmaIndex.get(i);
            int end = content.indexOf(" ", start);
            int nextPoint = i + 1;
            while (nextPoint < lemmaIndex.size()
                    && lemmaIndex.get(nextPoint) - end > 0
                    && lemmaIndex.get(nextPoint) - end < 5) {
                end = content.indexOf(" ", lemmaIndex.get(nextPoint));
                nextPoint += 1;
            }
            i = nextPoint - 1;
            var text = getWordsFromIndex(start, end, content);
            result.add(text);
        }
        result.sort(Comparator.comparingInt(String::length).reversed());
        return result;
    }

    private String getWordsFromIndex(int start, int end, String content) {
        var word = content.substring(start, end);
        int prevPoint;
        int lastPoint;
        if (content.lastIndexOf(" ", start) != -1) {
            prevPoint = content.lastIndexOf(" ", start);
        } else {
            prevPoint = start;
        }
        if (content.indexOf(" ", end + 30) != -1) {
            lastPoint = content.indexOf(" ", end + 30);
        } else {
            lastPoint = content.indexOf(" ", end);
        }
        var text = content.substring(prevPoint, lastPoint);
        try {
            text = text.replaceAll(word, "<b>" + word + "</b>");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return text;
    }

    private List<Lemma> getLemmaListFromSite(List<String> lemmas, Site site) {
        var lemmaList = lemmaRepository.findLemmaListBySite(lemmas, site);
        List<Lemma> result = new ArrayList<>(lemmaList);
        result.sort(Comparator.comparingInt(Lemma::getFrequency));
        return result;
    }

    private List<String> getLemmaFromText(String searchText) {
        var elements = searchText.toLowerCase(Locale.ROOT).split("\\s+");
        List<String> lemmas = new ArrayList<>();
        for (String el : elements) {
            var lemmaWord = morphology.getLemma(el);
            lemmas.addAll(lemmaWord);
        }
        return lemmas;
    }

    private LinkedHashMap<Page, Float> getPageAbsRelevance(List<Page> pageList, List<Index> indexList) {
        Map<Page, Float> pageWithRelevance = new HashMap<>();
        for (Page page : pageList) {
            float relevant = 0;
            for (Index index : indexList) {
                if (index.getPage() == page) {
                    relevant += index.getWordRank();
                }
            }
            pageWithRelevance.put(page, relevant);
        }
        Map<Page, Float> pageWithAbsRelevance = new HashMap<>();
        for (Page page : pageWithRelevance.keySet()) {
            float absRelevant = pageWithRelevance.get(page) / Collections.max(pageWithRelevance.values());
            pageWithAbsRelevance.put(page, absRelevant);
        }
        return pageWithAbsRelevance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private List<SearchDto> getSearchData(LinkedHashMap<Page, Float> pageList, List<String> textLemmaList) {
        List<SearchDto> result = new ArrayList<>();
        for (Page page : pageList.keySet()) {
            var uri = page.getPath();
            var content = page.getContent();
            var pageSite = page.getSite();
            var siteUrl = pageSite.getUrl();
            var siteName = pageSite.getName();
            var absRelevance = pageList.get(page);

            StringBuilder clearContent = new StringBuilder();
            var title = ClearHtmlCode.clear(content, TITLE_SELECTOR);
            var body = ClearHtmlCode.clear(content, BODY_SELECTOR);
            clearContent.append(title).append(" ").append(body);
            var snippet = getSnippet(clearContent.toString(), textLemmaList);

            result.add(new SearchDto(siteUrl, siteName, uri, title, snippet, absRelevance));
        }
        return result;
    }
}
