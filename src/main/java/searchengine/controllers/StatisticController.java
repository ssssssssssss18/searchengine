package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.responce.StatisticsResponse;
import searchengine.services.StatisticService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistics() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new StatisticsResponse(true, statisticService.getStatistics()));
    }
}
