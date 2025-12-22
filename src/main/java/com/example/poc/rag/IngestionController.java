package main.java.com.example.poc.rag;

import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class IngestionController {

    private final VectorStore vectorStore;

    public IngestionController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("/ingest")
    public String ingest(@RequestBody IngestRequest req) {

        if (req == null || req.text() == null || req.text().isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }

        var metadata = (req.metadata() == null) ? Map.<String, Object>of() : req.metadata();

        Document doc = new Document(req.text(), metadata);
        vectorStore.add(java.util.List.of(doc));

        return "OK";
    }

    public record IngestRequest(String text, Map<String, Object> metadata) {}

    @PostMapping("/ingest-file")
    public String ingestFile(@RequestParam("path") String path) throws Exception {
        var text = java.nio.file.Files.readString(java.nio.file.Path.of(path));
        vectorStore.add(java.util.List.of(new Document(text, Map.of("source", path))));
        return "Documents Ingested!";
}
}
