package com.careerit.saiopenai.service;

import com.careerit.saiopenai.domain.GenerateContent;
import com.careerit.saiopenai.domain.McqQuestion;
import com.careerit.saiopenai.domain.ShortSummary;
import com.careerit.saiopenai.domain.TrueOrFalseQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import jakarta.annotation.PreDestroy;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaContentGenerationServiceWithCompletableFeature {

       private final PromptServiceImpl promptService;
       private final ChatClient chatClient;
       
       // Create a custom executor for async operations
       private final ExecutorService executor = Executors.newFixedThreadPool(4);

        public GenerateContent generateContent() {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                String systemPrompt = promptService.getPromptWithReplaceMetaData("System");
                String summaryPrompt = promptService.getPromptWithReplaceMetaData("Summary");

                // First, get the summary response (this needs to complete first as it's used by others)
                CompletableFuture<String> summaryFuture = getSummaryAsync(systemPrompt, summaryPrompt);
                String summaryResponse = summaryFuture.get();

                // Now run the other operations in parallel
                CompletableFuture<ShortSummary> shortSummaryFuture = getShortSummaryAsync(systemPrompt, summaryResponse);
                CompletableFuture<List<McqQuestion>> mcqFuture = getMcqQuestionsAsync(systemPrompt, summaryResponse);
                CompletableFuture<List<TrueOrFalseQuestion>> trueOrFalseFuture = getTrueOrFalseQuestionsAsync(systemPrompt, summaryResponse);

                // Wait for all parallel operations to complete
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                        shortSummaryFuture, mcqFuture, trueOrFalseFuture);

                allFutures.get(); // Wait for all to complete

                // Build the final result
                GenerateContent generateContent = GenerateContent
                        .builder()
                        .largeSummary(summaryResponse)
                        .mcqQuestions(mcqFuture.get())
                        .shortSummary(shortSummaryFuture.get())
                        .trueOrFalseQuestions(trueOrFalseFuture.get())
                        .build();
                stopWatch.stop();
                log.info("Generated content in {} ms", stopWatch.getTime());
                return generateContent;
            } catch (Exception e) {
                throw new RuntimeException("Error generating content: " + e.getMessage(), e);
            }
        }

        private CompletableFuture<String> getSummaryAsync(String systemPrompt, String summaryPrompt) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Generating content for system prompt");
                    return this.chatClient
                            .prompt()
                            .system(systemPrompt)
                            .user(summaryPrompt)
                            .call()
                            .content();
                } catch (Exception e) {
                    throw new RuntimeException("Error getting summary: " + e.getMessage(), e);
                }
            }, executor);
        }

        private CompletableFuture<ShortSummary> getShortSummaryAsync(String systemPrompt, String summaryResponse) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Generating content for short summary prompt");
                    String shortSummary = promptService.getPromptWithReplaceMetaData("ShortSummary");
                    return this.chatClient
                            .prompt()
                            .messages(new SystemMessage(systemPrompt),
                                    new AssistantMessage(summaryResponse), new UserMessage(shortSummary))
                            .call()
                            .entity(new ParameterizedTypeReference<ShortSummary>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Error getting short summary: " + e.getMessage(), e);
                }
            }, executor);
        }

        private CompletableFuture<List<McqQuestion>> getMcqQuestionsAsync(String systemPrompt, String summaryResponse) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Generating content for mcq question prompt");
                    String mcqPrompt = promptService.getPromptWithReplaceMetaData("MCQ");
                    return this.chatClient
                            .prompt()
                            .messages(new SystemMessage(systemPrompt), 
                                    new AssistantMessage(summaryResponse), new UserMessage(mcqPrompt))
                            .call()
                            .entity(new ParameterizedTypeReference<List<McqQuestion>>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Error getting MCQ questions: " + e.getMessage(), e);
                }
            }, executor);
        }

        private CompletableFuture<List<TrueOrFalseQuestion>> getTrueOrFalseQuestionsAsync(String systemPrompt, String summaryResponse) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    log.info("Generating content for true or false question prompt");
                    String trueOrFalsePrompt = promptService.getPromptWithReplaceMetaData("TrueOrFalse");
                    return this.chatClient
                            .prompt()
                            .messages(new SystemMessage(systemPrompt), 
                                    new AssistantMessage(summaryResponse), new UserMessage(trueOrFalsePrompt))
                            .call()
                            .entity(new ParameterizedTypeReference<List<TrueOrFalseQuestion>>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Error getting True/False questions: " + e.getMessage(), e);
                }
            }, executor);
        }

        @PreDestroy
        public void shutdown() {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
        }
}
