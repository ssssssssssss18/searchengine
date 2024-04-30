package searchengine.services;

public interface IndexService {

    boolean startIndexing();

    boolean stopIndexing();

    void indexPage();
}
