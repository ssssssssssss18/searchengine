package searchengine.dto.statistics;

import java.util.List;

public record Statistics(Total total, List<Detailed> detailed) {
}
