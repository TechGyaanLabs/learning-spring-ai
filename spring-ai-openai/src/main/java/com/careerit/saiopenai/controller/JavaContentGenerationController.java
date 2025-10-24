package com.careerit.saiopenai.controller;

import com.careerit.saiopenai.domain.GenerateContent;
import com.careerit.saiopenai.service.JavaContentGenerationService;
import com.careerit.saiopenai.service.JavaContentGenerationServiceWithCompletableFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/java")
@RequiredArgsConstructor
public class JavaContentGenerationController {

        private final JavaContentGenerationServiceWithCompletableFeature javaContentGenerationService;

        @GetMapping("/generate-content")
        public GenerateContent generateContent(){
            return javaContentGenerationService.generateContent();
        }
}
