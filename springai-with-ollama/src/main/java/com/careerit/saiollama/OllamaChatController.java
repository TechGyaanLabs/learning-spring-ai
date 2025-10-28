package com.careerit.saiollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/v1/ollama")

public class OllamaChatController {

    private final ChatClient chatClient;

    public OllamaChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(
            name = "message",defaultValue = "Tell more about you?") String userPrompt,
            @RequestHeader("userid") String userId) {
        return
                chatClient.
                        prompt(userPrompt)
                        .advisors((advisorsSpec)->{
                            advisorsSpec.param(CONVERSATION_ID, userId);
                        })
                        .call().content();
    }
}
