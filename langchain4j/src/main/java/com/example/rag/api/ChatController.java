package com.example.rag.api;

import com.example.rag.service.RagChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChatController {

    public record ChatRequest(String question, Integer topK) {}
    public record ChatResponse(String answer) {}

    private final RagChatService ragChatService;

    public ChatController(RagChatService ragChatService) {
        this.ragChatService = ragChatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest req) {
        int k = (req.topK() == null || req.topK() <= 0) ? 5 : req.topK();
        return new ChatResponse(ragChatService.ask(req.question(), k));
    }
}
