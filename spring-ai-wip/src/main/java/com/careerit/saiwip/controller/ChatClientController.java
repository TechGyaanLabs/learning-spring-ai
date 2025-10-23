package com.careerit.saiwip.controller;

import com.careerit.saiwip.service.TemplateService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ChatClientController {

        private final ChatClient chatClient;
        private final TemplateService templateService;

        public ChatClientController(ChatClient.Builder chatClient, TemplateService templateService) {
            this.chatClient = chatClient.build();
            this.templateService = templateService;
        }

        @GetMapping("/hr-policies")
        public String hrPolicies(@RequestParam("message") String message) {
                return chatClient
                        .prompt()
                        .system(templateService.getTemplate("hr-policies"))
                        .user(message)
                        .call()
                        .content();
        }

        @GetMapping("/it-helpdesk")
        public String itHelpDesk(@RequestParam("message") String message) {
                return chatClient
                        .prompt()
                        .system(templateService.getTemplate("it-helpdesk"))
                        .user(message)
                        .call()
                        .content();
        }
        
        @GetMapping("/templates")
        public Map<String, String> getAllTemplates() {
                return templateService.getAllTemplates();
        }
}
