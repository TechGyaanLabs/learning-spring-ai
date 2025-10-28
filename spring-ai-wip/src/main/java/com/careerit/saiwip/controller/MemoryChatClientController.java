package com.careerit.saiwip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MemoryChatClientController {

    private final ChatClient chatClient;
    @GetMapping("/chat")
    public String chat(@RequestParam("message")String message, @RequestHeader("username")String username) {
        return
                chatClient
                        .prompt(message)
                        .advisors((advisorSpec -> {
                            advisorSpec.param(CONVERSATION_ID, username);
                        }))
                        .call()
                        .content();
    }
}
