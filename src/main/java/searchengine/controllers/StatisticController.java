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
        var statistic = statisticService.getStatistics();
        return new ResponseEntity<>(new StatisticResponse(true, statistic), HttpStatus.OK);
    }
}
