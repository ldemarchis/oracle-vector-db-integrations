package com.example.rag.api;

import com.example.rag.service.IngestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IngestController {

    public record IngestRequest(String docId, String text, Map<String, Object> metadata) {}
    public record IngestResponse(List<String> ids) {}

    private final IngestionService ingestionService;

    public IngestController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/ingest")
    public IngestResponse ingest(@RequestBody IngestRequest req) {
        var ids = ingestionService.ingest(req.docId(), req.text(), req.metadata());
        return new IngestResponse(ids);
    }
}
