package searchengine.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.dto.IndexDto;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.morphology.Morphology;
import searchengine.repository.FieldRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.utils.ClearHtmlCode;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Indexing implements IndexParser {
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;
    private final Morphology morphology;
    private List<IndexDto> indexDtoList;

    @Override
    public void run(Site site) {
        var pageList = pageRepository.findBySite(site);
        var lemmaList = lemmaRepository.findBySite(site);
        var fieldList = fieldRepository.findAll();
        indexDtoList = new ArrayList<>();

        for (Page page : pageList) {
            if (page.getStatusCode() == 200) {
                var pageId = page.getId();
                var content = page.getContent();
                var title = ClearHtmlCode.clear(content, fieldList.get(0).getSelector());
                var body = ClearHtmlCode.clear(content, fieldList.get(1).getSelector());
                var titleList = morphology.getLemmaList(title);
                var bodyList = morphology.getLemmaList(body);

                for (Lemma lemma : lemmaList) {
                    var lemmaId = lemma.getId();
                    var keyWord = lemma.getLemma();
                    if (titleList.containsKey(keyWord) || bodyList.containsKey(keyWord)) {
                        float totalRank = 0.0F;
                        if (titleList.get(keyWord) != null) {
                            var titleRank = Float.valueOf(titleList.get(keyWord));
                            totalRank += titleRank;
                        }
                        if (bodyList.get(keyWord) != null) {
                            var bodyRank = (float) (bodyList.get(keyWord) * 0.8);
                            totalRank += bodyRank;
                        }
                        indexDtoList.add(new IndexDto(pageId, lemmaId, totalRank));
                    } else {
                        log.debug("Лемма не найдена");
                    }
                }
            } else {
                log.debug("Неуспешный статус код - " + page.getStatusCode());
            }
        }
    }

    @Override
    public List<IndexDto> getIndexList() {
        return indexDtoList;
    }
}
