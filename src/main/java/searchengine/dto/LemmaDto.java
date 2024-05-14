package searchengine.dto;

/**
 * Модель леммы
 *
 * @param lemma     нормальная форма слова
 * @param frequency количество страниц, на которых слово встречается хотя бы один раз.
 *                  Максимальное значение не может превышать общее количество слов на сайте
 */
public record LemmaDto(String lemma, int frequency) {
}
