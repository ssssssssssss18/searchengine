package searchengine.morphology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class LuceneMorphology implements Morphology {
    private static org.apache.lucene.morphology.LuceneMorphology russianMorph;
    private final static String regex = "\\p{Punct}|[0-9]|@|©|◄|»|«|—|-|№|…";
    private final static Logger logger = LogManager.getLogger(LuceneMorphology.class);
    private final static Marker INVALID_SYMBOL_MARKER = MarkerManager.getMarker("INVALID_SYMBOL");
    private static final Set<String> SERVICE_PARTS_OF_SPEECH = Set.of("ПРЕДЛ", "СОЮЗ", "МЕЖД", "МС", "ЧАСТ");
    private static final int MAX_LENGTH = 3;

    static {
        try {
            russianMorph = new RussianLuceneMorphology();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public HashMap<String, Integer> getLemmaList(String content) {
        content = content.toLowerCase(Locale.ROOT).replaceAll(regex, " ");

        return Arrays.stream(content.split("\\s+"))
                .flatMap(element -> getLemma(element).stream())
                .collect(HashMap::new,
                        (map, word) -> map.merge(word, 1, Integer::sum),
                        HashMap::putAll);
    }

    @Override
    public List<String> getLemma(String word) {
        List<String> lemmaList = new ArrayList<>();
        try {
            var baseRusForm = russianMorph.getNormalForms(word);
            if (!isServiceWord(word)) {
                lemmaList.addAll(baseRusForm);
            }
        } catch (Exception e) {
            logger.debug(INVALID_SYMBOL_MARKER, "Символ не найден - " + word);
        }
        return lemmaList;
    }

    @Override
    public List<Integer> findLemmaIndexInText(String content, String lemma) {
        List<Integer> lemmaIndexList = new ArrayList<>();
        var elements = content.toLowerCase(Locale.ROOT).split("\\p{Punct}|\\s");
        int index = 0;
        for (String el : elements) {
            var lemmas = getLemma(el);
            for (String lem : lemmas) {
                if (lem.equals(lemma)) {
                    lemmaIndexList.add(index);
                }
            }
            index += el.length() + 1;
        }
        return lemmaIndexList;
    }

    private boolean isServiceWord(String word) {
        return russianMorph.getMorphInfo(word).stream()
                .anyMatch(morphInfo -> SERVICE_PARTS_OF_SPEECH.contains(morphInfo)
                        || morphInfo.length() <= MAX_LENGTH);
    }
}
