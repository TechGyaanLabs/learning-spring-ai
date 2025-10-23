package com.careerit.saiopenai.domain;

import lombok.Data;

import java.util.List;

@Data
public class McqQuestion {

    private String question;
    private List<Option> options;
    private String answer;
    private String explanation;
}
