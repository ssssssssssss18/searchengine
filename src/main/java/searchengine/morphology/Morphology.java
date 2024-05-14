package searchengine.morphology;

import java.util.HashMap;
import java.util.List;

/**
 * Сервис для работы с леммами
 */
public interface Morphology {

    /**
     * Получение карты слов (лемм)
     *
     * @param content контент
     * @return карта лемм - лемма + ее частота
     */
    HashMap<String, Integer> getLemmaList(String content);

    /**
     * Получение списка лемм
     *
     * @param word слово (лемма)
     * @return список лемм
     */
    List<String> getLemma(String word);

    /**
     * Получение списка частоты леммы
     *
     * @param content контент
     * @param lemma   лемма
     * @return список числа леммы
     */
    List<Integer> findLemmaIndexInText(String content, String lemma);
}
