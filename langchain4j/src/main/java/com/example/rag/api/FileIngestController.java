package com.example.rag.api;

import com.example.rag.service.FileIngestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FileIngestController {

    public record FileIngestResponse(int filesIngested) {}

    private final FileIngestionService fileIngestionService;

    public FileIngestController(FileIngestionService fileIngestionService) {
        this.fileIngestionService = fileIngestionService;
    }

    @PostMapping("/ingest/files")
    public FileIngestResponse ingestFiles() throws Exception {
        int n = fileIngestionService.ingestAll();
        return new FileIngestResponse(n);
    }
}
