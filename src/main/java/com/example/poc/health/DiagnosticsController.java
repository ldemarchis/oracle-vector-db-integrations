package main.java.com.example.poc.health;

import javax.sql.DataSource;
import java.sql.Connection;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagnosticsController {

    private final DataSource dataSource;
    private final ChatModel chatModel;

    public DiagnosticsController(DataSource dataSource, ChatModel chatModel) {
        this.dataSource = dataSource;
        this.chatModel = chatModel;
    }

    @GetMapping("/diag")
    public String diagnostics() {
        boolean dbOk = checkDatabase();
        boolean llmOk = checkLlm();

        return """
            Database reachable: %s
            LLM reachable: %s
            """.formatted(dbOk, llmOk);
    }

    private boolean checkDatabase() {
        try (Connection c = dataSource.getConnection()) {
            return c.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkLlm() {
        try {
            chatModel.call("ping");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
