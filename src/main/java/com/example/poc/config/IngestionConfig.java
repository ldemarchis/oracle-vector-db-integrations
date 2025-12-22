package java.main.com.example.poc.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

@Configuration
public class IngestionConfig {

    @Bean
    public Runnable ingestSeedDocuments(VectorStore vectorStore) {
        return () -> {
            try {
                var resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources("classpath:docs/seed/*.txt");

                List<Document> documents = java.util.Arrays.stream(resources)
                        .map(this::toDocument)
                        .toList();

                if (!documents.isEmpty()) {
                    vectorStore.add(documents);
                }

            } catch (IOException e) {
                throw new IllegalStateException("Failed to ingest seed documents", e);
            }
        };
    }

    private Document toDocument(Resource resource) {
        try {
            String content = StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            return new Document(
                    content,
                    java.util.Map.of("source", resource.getFilename())
            );

        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + resource.getFilename(), e);
        }
    }
}
