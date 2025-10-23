package com.careerit.saiopenai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateContent {
        private String largeSummary;
        private ShortSummary shortSummary;
        private List<McqQuestion> mcqQuestions;
}
