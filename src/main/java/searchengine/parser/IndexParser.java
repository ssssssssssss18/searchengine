package searchengine.parser;

import searchengine.dto.IndexDto;
import searchengine.model.Site;

import java.util.List;

public interface IndexParser {

    void run(Site site);
    List<IndexDto> getIndexList();
}
