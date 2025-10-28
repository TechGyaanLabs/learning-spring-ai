package com.careerit.saiwip.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TextSplitterConfig {

    @Autowired
    private JdbcChatMemoryRepository chatMemoryRepository;

    @Bean
    public TokenTextSplitter textSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    public ChatMemory chatMemory() {
       return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        Advisor chatMemoryAdvisor
                = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
        return chatClientBuilder
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),chatMemoryAdvisor))
                .build();
    }
}
