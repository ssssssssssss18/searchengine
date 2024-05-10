package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.responce.FalseResponse;
import searchengine.dto.responce.TrueResponse;
import searchengine.services.IndexService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IndexController {

    private final IndexService indexService;

    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexing() {
        var startIndexing = indexService.startIndexing();
        return ResponseEntity.status(startIndexing ? HttpStatus.OK : HttpStatus.METHOD_NOT_ALLOWED)
                .body(startIndexing ? new TrueResponse(true) :
                        new FalseResponse(false, "Индексация уже запущена"));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        var stopIndexing = indexService.stopIndexing();
        return ResponseEntity.status(stopIndexing ? HttpStatus.OK : HttpStatus.METHOD_NOT_ALLOWED)
                .body(stopIndexing ? new TrueResponse(true) :
                        new FalseResponse(false, "Индексация не запущена"));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> indexPage(String url) {
        var indexPage = indexService.indexPage(url);
        return ResponseEntity.status(indexPage ? HttpStatus.OK : HttpStatus.METHOD_NOT_ALLOWED)
                .body(indexPage ? new TrueResponse(true) :
                        new FalseResponse(false, "Данная страница находится за пределами сайтов"));
    }
}
