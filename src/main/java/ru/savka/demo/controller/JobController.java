package ru.savka.demo.controller;

import ru.savka.demo.dto.ApiResponseDto;
import ru.savka.demo.dto.FetchJobStatusDto;
import ru.savka.demo.service.FetchService;
import ru.savka.demo.service.ParserService;
import ru.savka.demo.service.ThreadManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final FetchService fetchService;
    private final ParserService parserService;
    private final ThreadManagerService threadManagerService;

    public JobController(FetchService fetchService, ParserService parserService, ThreadManagerService threadManagerService) {
        this.fetchService = fetchService;
        this.parserService = parserService;
        this.threadManagerService = threadManagerService;
    }

    @GetMapping("/fetch/start")
    public ResponseEntity<ApiResponseDto> startFetching() {
        fetchService.startFetching();
        return ResponseEntity.ok(new ApiResponseDto("Fetching started.", true));
    }

    @GetMapping("/fetch/stop")
    public ResponseEntity<ApiResponseDto> stopFetching() {
        fetchService.stopFetching();
        return ResponseEntity.ok(new ApiResponseDto("Fetching stopped.", true));
    }

    @GetMapping("/parse/start")
    public ResponseEntity<ApiResponseDto> startParsing() {
        parserService.startParsing();
        return ResponseEntity.ok(new ApiResponseDto("Parsing started.", true));
    }

    @GetMapping("/parse/stop")
    public ResponseEntity<ApiResponseDto> stopParsing() {
        parserService.stopParsing();
        return ResponseEntity.ok(new ApiResponseDto("Parsing stopped.", true));
    }

    @GetMapping("/status")
    public ResponseEntity<FetchJobStatusDto> getJobStatus() {
        FetchJobStatusDto status = new FetchJobStatusDto(
                threadManagerService.isFetchExecutorRunning(),
                threadManagerService.getActiveFetchTasks(),
                threadManagerService.getActiveParserTasks(),
                threadManagerService.getQueuedFetchTasks(),
                threadManagerService.getQueuedParserTasks(),
                threadManagerService.isFetchExecutorRunning() || threadManagerService.isParserExecutorRunning() ? "RUNNING" : "STOPPED"
        );
        return ResponseEntity.ok(status);
    }
}
