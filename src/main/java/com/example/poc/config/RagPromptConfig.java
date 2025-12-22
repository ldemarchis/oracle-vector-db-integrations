package main.java.com.example.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagPromptConfig {

    @Bean
    public String ragPromptTemplate() {
        return """
            You are a technical assistant.
            Answer the question strictly using the provided context.
            If the context does not contain the answer, say that you do not know.

            Context:
            {context}

            Question:
            {question}
            """;
    }
}
