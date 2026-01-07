package com.example.rag.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.oracle.CreateOption;
import dev.langchain4j.store.embedding.oracle.IVFIndexBuilder;
import dev.langchain4j.store.embedding.oracle.OracleEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LangChainConfig {

    @Bean
    EmbeddingModel embeddingModel(
            @Value("${app.ollama.base-url}") String baseUrl,
            @Value("${app.ollama.embedding-model}") String embeddingModelName
    ) {
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(embeddingModelName)
                .build();
    }

    @Bean
    ChatModel chatModel(
            @Value("${app.ollama.base-url}") String baseUrl,
            @Value("${app.ollama.chat-model}") String chatModelName
    ) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(chatModelName)
                .temperature(0.2)
                .build();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(
            DataSource dataSource,
            @Value("${app.oracle.embedding-table}") String tableName,
            @Value("${app.oracle.create-schema:true}") boolean createSchema
    ) {
        var builder = OracleEmbeddingStore.builder()
                .dataSource(dataSource);

        if (createSchema) {
            builder.embeddingTable(tableName, CreateOption.CREATE_IF_NOT_EXISTS);
        } else {
            builder.embeddingTable(tableName);
        }

        return builder.build();
    }
}
