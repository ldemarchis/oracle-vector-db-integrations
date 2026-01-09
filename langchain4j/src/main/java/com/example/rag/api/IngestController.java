package com.example.rag.api;

import com.example.rag.service.IngestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IngestController {

    public record IngestTextRequest(String docId, String text, Map<String, Object> metadata) {}
    public record IngestPathRequest(String path, String docId, Map<String, Object> metadata) {}
    public record IngestResponse(String docId, int chunks, List<String> ids) {}

    private final IngestionService ingestionService;

    public IngestController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/ingest")
    public IngestResponse ingestText(@RequestBody IngestTextRequest req) {
        List<String> ids = ingestionService.ingest(req.docId(), req.text(), req.metadata());
        return new IngestResponse(req.docId(), ids.size(), ids);
    }

    @PostMapping("/ingest/path")
    public IngestResponse ingestPath(@RequestBody IngestPathRequest req) throws Exception {
        var r = ingestionService.ingestPath(req.path(), req.docId(), req.metadata());
        return new IngestResponse(r.docId(), r.chunks(), r.ids());
    }
}