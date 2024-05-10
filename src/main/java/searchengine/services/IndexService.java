package searchengine.services;

public interface IndexService {

    boolean startIndexing();

    boolean stopIndexing();

    boolean indexPage(String url);
}
