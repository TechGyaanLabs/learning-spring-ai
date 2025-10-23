package com.careerit.saiopenai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@RestController
@RequestMapping("/api/v1/content")
public class ContentGenerationController {

        private final ChatClient chatClient;

        @Value("classpath:quiz-questions.st")
        private Resource quizResource;


        public ContentGenerationController(ChatClient.Builder chatClient) {
            this.chatClient = chatClient
                    .defaultSystem("You are java expert, create content for the java related topics")
                    .build();
        }

        @PostMapping("/chat")
        public String chat(@RequestBody ContentRequest contentRequest) {
                return this.chatClient
                        .prompt()
                        .user((promptUserSpec) ->
                            {
                                promptUserSpec.
                                        text(quizResource)
                                        .param("wordsCount",contentRequest.wordsCount())
                                        .param("noMcq",contentRequest.noMcq())
                                        .param("topic",contentRequest.topic());
                            }).call().content();
        }

}

record ContentRequest(String topic,int wordsCount, int noMcq){}
