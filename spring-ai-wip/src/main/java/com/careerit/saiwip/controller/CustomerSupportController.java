package com.careerit.saiwip.controller;

import com.careerit.saiwip.service.TemplateService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CustomerSupportController {

        private final ChatClient chatClient;
        private final TemplateService templateService;

        public CustomerSupportController(ChatClient.Builder chatClient, TemplateService templateService) {
                this.chatClient = chatClient.build();
                this.templateService = templateService;
        }

        @PostMapping("/customer-support/email")
        public String generateEmailResponse(@RequestParam String name, 
                                         @RequestParam String message) {
                Map<String, Object> variables = Map.of("name", name, "message", message);
                String formattedPrompt = templateService.getFormattedPrompt("email-response", variables);
                
                return chatClient
                        .prompt()
                        .system(templateService.getTemplate("email-body-template"))
                        .user(formattedPrompt)
                        .call()
                        .content();
        }

        @GetMapping("/customer-support/templates")
        public Map<String, String> getCustomerSupportTemplates() {
                return templateService.getAllTemplates();
        }
}
