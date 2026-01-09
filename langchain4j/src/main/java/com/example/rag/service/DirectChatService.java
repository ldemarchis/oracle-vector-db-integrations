package com.example.rag.service;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class DirectChatService {

    private final ChatModel chatModel;

    public DirectChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return "Please provide a prompt.";
        }
        return chatModel.chat(prompt);
    }
}
