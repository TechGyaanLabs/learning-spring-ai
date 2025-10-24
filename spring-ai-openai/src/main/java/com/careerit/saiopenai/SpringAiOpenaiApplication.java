package com.careerit.saiopenai;

import com.careerit.saiopenai.service.PromptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiOpenaiApplication{

    @Autowired
    private PromptServiceImpl promptService;


    public static void main(String[] args) {
		SpringApplication.run(SpringAiOpenaiApplication.class, args);
	}

}
