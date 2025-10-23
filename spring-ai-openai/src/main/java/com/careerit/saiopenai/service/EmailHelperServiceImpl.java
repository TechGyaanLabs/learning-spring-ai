package com.careerit.saiopenai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.TemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailHelperServiceImpl {

    @Value("classpath:prompts/email-system-prompt.st")
    private Resource systemPrompt;

    @Value("classpath:prompts/email-user-prompt.st")
    private Resource userPrompt;


    private final ChatClient chatClient;

    public String getEmailBody(String name, String message) {
        return
                chatClient.prompt()
                        .system(systemPrompt)
                        .user((promptUserSpec -> {
                            promptUserSpec.text(userPrompt)
                                    .param("name", name)
                                    .param("message", message);

                        }))
                        .call()
                        .content();
    }

}
