package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import searchengine.dto.responce.FalseResponse;
import searchengine.dto.responce.TrueResponse;
import searchengine.services.IndexService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IndexController {

    private final IndexService indexService;

    @GetMapping("/startIndexing")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> startIndexing() {
        if (indexService.startIndexing()) {
            return new ResponseEntity<>(new TrueResponse(true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new FalseResponse(false, "Индексация уже запущена"),
                    HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        if (indexService.stopIndexing()) {
            return new ResponseEntity<>(new TrueResponse(true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new FalseResponse(false, "Индексация не запущена"),
                    HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping("/indexPage")
    public void indexPage() {
        indexService.indexPage();
    }
}
