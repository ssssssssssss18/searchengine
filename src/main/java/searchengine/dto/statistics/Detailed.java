package searchengine.dto.statistics;

import searchengine.model.Status;

import java.time.LocalDateTime;

public record Detailed(String url, String name, Status status, LocalDateTime statusTime, String error, Long pages,
                       Long lemmas) {
}
