package com.example.rag.api;

import com.example.rag.service.FileIngestionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileIngestController {

    public record IngestPathRequest(String path, String docId, Map<String, Object> metadata) {}
    public record IngestPathResponse(String docId, int chunks, List<String> ids) {}

    private final FileIngestionService fileIngestionService;

    public FileIngestController(FileIngestionService fileIngestionService) {
        this.fileIngestionService = fileIngestionService;
    }

    @PostMapping("/ingest/path")
    public IngestPathResponse ingestPath(@RequestBody IngestPathRequest req) throws Exception {
        var r = fileIngestionService.ingestPath(req.path(), req.docId(), req.metadata());
        return new IngestPathResponse(r.docId(), r.chunks(), r.ids());
    }
}
