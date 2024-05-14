package searchengine.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import searchengine.dto.LemmaDto;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.morphology.Morphology;
import searchengine.repository.PageRepository;
import searchengine.utils.ClearHtmlCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LemmaConversion implements LemmaParser {
    private List<LemmaDto> lemmaDtoList;
    @Value("${title-selector}")
    private String TITLE_SELECTOR;
    @Value("${body-selector}")
    private String BODY_SELECTOR;

    private final PageRepository pageRepository;
    private final Morphology morphology;

    @Override
    public List<LemmaDto> getLemmaDtoList() {
        return lemmaDtoList;
    }

    @Override
    public void run(Site site) {
        lemmaDtoList = new ArrayList<>();
        List<Page> pageList = pageRepository.findBySite(site);
        log.info("pageList: " + pageList);
        HashMap<String, Integer> lemmaList = new HashMap<>();
        for (Page page : pageList) {
            var content = page.getContent();
            var title = ClearHtmlCode.clear(content, TITLE_SELECTOR);
            var body = ClearHtmlCode.clear(content, BODY_SELECTOR);
            var titleList = morphology.getLemmaList(title);
            var bodyList = morphology.getLemmaList(body);

            Set<String> allWords = new HashSet<>();
            allWords.addAll(titleList.keySet());
            allWords.addAll(bodyList.keySet());

            allWords.forEach(word -> {
                int frequency = lemmaList.getOrDefault(word, 0);
                lemmaList.put(word, frequency + 1);
            });
        }

        lemmaList.keySet().forEach(lemma -> {
            var frequency = lemmaList.get(lemma);
            lemmaDtoList.add(new LemmaDto(lemma, frequency));
        });
    }
}
