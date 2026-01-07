package com.example.rag.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagChatService {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public RagChatService(ChatModel chatModel,
                          EmbeddingModel embeddingModel,
                          EmbeddingStore<TextSegment> embeddingStore) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public String ask(String question, int topK) {
        var queryEmbedding = embeddingModel.embed(question).content();

        var req = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(topK)
                .build();

        var result = embeddingStore.search(req);

        String context = result.matches().stream()
                .map(m -> m.embedded().text())
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = """
                You are a helpful assistant.
                Use ONLY the provided context. If the context does not contain the answer, say you don't know.

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

/*         var response = chatModel.generate(List.of(
                SystemMessage.from(system),
                UserMessage.from(user)
        ));
*/
        return chatModel.chat(prompt);
    }
}
