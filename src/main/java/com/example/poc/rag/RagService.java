package com.example.poc.rag;

import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final String promptTemplate;

    public RagService(VectorStore vectorStore, ChatModel chatModel, String ragPromptTemplate) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.promptTemplate = ragPromptTemplate;
    }

    public String ask(String question) {

        var searchRequest = SearchRequest.builder()
                .query(question)
                .topK(4)
                .build();

        var searchResults = vectorStore.similaritySearch(searchRequest);

        String context = searchResults.stream()
                .map(org.springframework.ai.document.Document::getText)
                .collect(Collectors.joining("\n\n"));

        var template = new PromptTemplate(promptTemplate);

        Prompt prompt = new Prompt(
                template.createMessage(
                        java.util.Map.of("context", context, "question", question)
                )
        );

        var response = chatModel.call(prompt);

        // In Spring AI 1.1.x the assistant message text is returned via getText()
        return response.getResult().getOutput().getText();
    }
}
