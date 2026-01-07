package com.example.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Service
public class FileIngestionService {

    private final IngestionService ingestionService;
    private final Path ingestDir;

    public FileIngestionService(IngestionService ingestionService,
                                @Value("${app.ingest.dir}") String ingestDir) {
        this.ingestionService = ingestionService;
        this.ingestDir = Paths.get(ingestDir);
    }

    public int ingestAll() throws IOException {
        if (!Files.exists(ingestDir) || !Files.isDirectory(ingestDir)) {
            throw new IllegalArgumentException("Ingest dir does not exist or is not a directory: " + ingestDir);
        }

        int count = 0;

        try (var paths = Files.walk(ingestDir)) {
            for (Path p : (Iterable<Path>) paths::iterator) {
                if (!Files.isRegularFile(p)) continue;

                String name = ingestDir.relativize(p).toString();
                String text = Files.readString(p, StandardCharsets.UTF_8);

                ingestionService.ingest(
                        name, // docId = filename/path
                        text,
                        Map.of("source", name)
                );

                count++;
            }
        }
        return count;
    }
}
