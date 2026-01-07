package com.example.rag.service;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IngestionService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final SimpleChunker simpleChunker;

    public IngestionService(EmbeddingModel embeddingModel,
                            EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.simpleChunker = new SimpleChunker();
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
}
