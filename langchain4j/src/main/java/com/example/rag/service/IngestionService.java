package com.example.rag.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@Service
public class IngestionService {

    public record IngestResult(String docId, int chunks, List<String> ids) {}

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final SimpleChunker simpleChunker;

    private final Path allowedDir; // optional safety

    public IngestionService(EmbeddingModel embeddingModel,
                            EmbeddingStore<TextSegment> embeddingStore,
                            @Value("${app.ingest.allowed-dir:}") String allowedDirStr) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.simpleChunker = new SimpleChunker();

        this.allowedDir = (allowedDirStr == null || allowedDirStr.isBlank())
                ? null
                : Paths.get(allowedDirStr).toAbsolutePath().normalize();
    }

    public List<String> ingest(String docId, String text, Map<String, Object> metadata) {
        List<String> chunks = simpleChunker.chunk(text, 1200, 150);
        List<String> ids = new ArrayList<>(chunks.size());

        int i = 0;
        for (String chunk : chunks) {
            Embedding emb = embeddingModel.embed(chunk).content();

            Metadata md = (metadata != null) ? new Metadata(metadata) : new Metadata();
            md.put("docId", docId);
            md.put("chunk", String.valueOf(i));

            TextSegment segment = TextSegment.from(chunk, md);
            String id = embeddingStore.add(emb, segment);
            ids.add(id);
            i++;
        }
        return ids;
    }

    public IngestResult ingestPath(String pathStr, String docIdOverride, Map<String, Object> metadata) throws Exception {
        if (pathStr == null || pathStr.isBlank()) {
            throw new IllegalArgumentException("path is required");
        }

        Path path = Paths.get(pathStr).normalize().toAbsolutePath();

        if (allowedDir != null && !path.startsWith(allowedDir)) {
            throw new IllegalArgumentException("Path is outside allowed ingest directory: " + allowedDir);
        }
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

        List<String> ids = ingest(docId, text, merged);
        return new IngestResult(docId, ids.size(), ids);
    }
}
