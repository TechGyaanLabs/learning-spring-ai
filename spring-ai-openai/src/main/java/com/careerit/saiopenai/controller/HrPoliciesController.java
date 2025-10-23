package com.careerit.saiopenai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hr")
@RequiredArgsConstructor
public class HrPoliciesController {


        @Value("classpath:prompts/hr-policies.st")
        private Resource promptResource;

        private final ChatClient chatClient;


        @GetMapping("/policies")
        public String hrPolicies(@RequestParam String message){
            return
                    chatClient
                            .prompt()
                            .system(promptResource)
                            .user(message)
                            .call()
                            .content();

        }
}
