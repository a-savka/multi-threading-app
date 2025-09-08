package ru.savka.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.savka.demo.dto.WorkerInfo;
import ru.savka.demo.service.WorkerService;

import java.util.List;

@RestController
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping("/workers")
    public List<WorkerInfo> getWorkers() {
        return workerService.getWorkers();
    }
}
