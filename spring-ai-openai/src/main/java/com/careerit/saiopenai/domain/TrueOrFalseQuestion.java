package com.careerit.saiopenai.domain;

import lombok.Data;

import java.util.List;

@Data
public class TrueOrFalseQuestion {
    private String question;
    private List<Boolean> options;
    private boolean correctAnswer;
    private String explanation;
}
