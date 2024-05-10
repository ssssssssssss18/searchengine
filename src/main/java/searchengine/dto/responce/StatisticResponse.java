package searchengine.dto.responce;

import searchengine.dto.statistics.Statistics;

public record StatisticResponse(boolean result, Statistics statistics) {
}
