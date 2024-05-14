package searchengine.model;

/**
 * Статусная модель
 */
public enum Status {

    /**
     * Индексирование
     */
    INDEXING,

    /**
     * Проиндексировано
     */
    INDEXED,

    /**
     * Ошибка индексирования
     */
    FAILED
}
