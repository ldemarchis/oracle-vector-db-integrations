package com.example.rag.api;

import com.example.rag.service.DirectChatService;
import com.example.rag.service.RagChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    public record ChatRequest(String question, Integer topK) {}
    public record ChatResponse(String answer) {}

    private final RagChatService ragChatService;
    private final DirectChatService directChatService;

    public ChatController(RagChatService ragChatService,
                          DirectChatService directChatService) {
        this.ragChatService = ragChatService;
        this.directChatService = directChatService;
    }

    // RAG chat
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest req) {
        int k = (req.topK() == null || req.topK() <= 0) ? 5 : req.topK();
        return new ChatResponse(ragChatService.ask(req.question(), k));
    }

    // No-RAG chat
    @PostMapping("/chat/direct")
    public ChatResponse chatDirect(@RequestBody ChatRequest req) {
        return new ChatResponse(directChatService.chat(req.question()));
    }
}
