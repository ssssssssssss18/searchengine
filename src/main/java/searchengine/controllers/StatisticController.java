package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.responce.StatisticResponse;
import searchengine.services.StatisticsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StatisticController {
    private final StatisticsService statisticService;

    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistic() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new StatisticResponse(true, statisticService.getStatistic()));
    }
}
