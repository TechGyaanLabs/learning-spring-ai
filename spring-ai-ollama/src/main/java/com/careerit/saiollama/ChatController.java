package com.careerit.saiollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ollama")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

        @GetMapping("/chat")
        public String chat(@RequestParam("message")String message){
                return this.chatClient
                        .prompt()
                        .user(message)
                        .call()
                        .content();
        }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return this.
                chatClient
                .prompt()
                .advisors(new SimpleLoggerAdvisor())
                .user(message)
                .stream()
                .chatResponse();
    }
}
