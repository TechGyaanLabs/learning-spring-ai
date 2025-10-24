package com.careerit.saiopenai.service;

import com.careerit.saiopenai.domain.GenerateContent;
import com.careerit.saiopenai.domain.McqQuestion;
import com.careerit.saiopenai.domain.ShortSummary;
import com.careerit.saiopenai.domain.TrueOrFalseQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JavaContentGenerationService {


       private final PromptServiceImpl promptService;
       private final ChatClient chatClient;

        public GenerateContent generateContent() {

            String systemPrompt = promptService.getPromptWithReplaceMetaData("System");
            String summaryPrompt = promptService.getPromptWithReplaceMetaData("Summary");

            String summaryResponse =
                    this.chatClient
                            .prompt()
                            .system(systemPrompt)
                            .user(summaryPrompt).call()
                            .content();


            String shortSummary = promptService.getPromptWithReplaceMetaData("ShortSummary");

            ShortSummary shortSummaryResponse = this.chatClient
                            .prompt()
                            .messages(new SystemMessage(systemPrompt),
                                    new AssistantMessage(summaryResponse),new UserMessage(shortSummary))
                            .call()
                    .entity(new ParameterizedTypeReference<ShortSummary>() {
                    });


            String mcqPrompt = promptService.getPromptWithReplaceMetaData("MCQ");

          List<McqQuestion> mcqQuestions = this.chatClient
                    .prompt()
                    .messages(new SystemMessage(systemPrompt),new AssistantMessage(summaryResponse),new UserMessage(mcqPrompt))
                    .call()
                    .entity(new ParameterizedTypeReference<List<McqQuestion>>() {});

          String trueOrFalsePrompt = promptService.getPromptWithReplaceMetaData("TrueOrFalse");
          List<TrueOrFalseQuestion> trueOrFalseQuestions = this.chatClient
                  .prompt()
                  .messages(new SystemMessage(systemPrompt),new AssistantMessage(summaryResponse),new UserMessage(trueOrFalsePrompt))
                  .call()
                  .entity(new ParameterizedTypeReference<List<TrueOrFalseQuestion>>() {
                  });

          GenerateContent generateContent =
                  GenerateContent
                          .builder()
                          .largeSummary(summaryResponse)
                          .mcqQuestions(mcqQuestions)
                          .shortSummary(shortSummaryResponse)
                          .trueOrFalseQuestions(trueOrFalseQuestions)
                          .build();

            return generateContent;
        }
}
