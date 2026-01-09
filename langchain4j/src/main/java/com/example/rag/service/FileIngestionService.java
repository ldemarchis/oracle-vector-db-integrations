package com.example.rag.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileIngestionService {

    private final IngestionService ingestionService;

    public FileIngestionService(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    public record IngestPathResult(String docId, int chunks, List<String> ids) {}

    public IngestPathResult ingestPath(String pathStr,
                                       String docIdOverride,
                                       Map<String, Object> metadata) throws Exception {

        if (pathStr == null || pathStr.isBlank()) {
            throw new IllegalArgumentException("path is required");
        }

        Path path = Paths.get(pathStr).normalize().toAbsolutePath();

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Not a regular file: " + path);
        }

        String text = Files.readString(path, StandardCharsets.UTF_8);

        String docId = (docIdOverride != null && !docIdOverride.isBlank())
                ? docIdOverride
                : path.getFileName().toString();

        Map<String, Object> merged = new HashMap<>();
        if (metadata != null) merged.putAll(metadata);
        merged.putIfAbsent("sourcePath", path.toString());

        List<String> ids = ingestionService.ingest(docId, text, merged);

        return new IngestPathResult(docId, ids.size(), ids);
    }
}
