package com.careerit.saiwip;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Map;

public class TemplateVariableReplacementTest {
    
    public static void main(String[] args) {
        // Load template
        Resource templateResource = new ClassPathResource("templates/email-response.st");
        PromptTemplate template = new PromptTemplate(templateResource);
        
        // Define variables
        Map<String, Object> variables = Map.of(
            "name", "John Doe",
            "message", "I need help with my order #12345"
        );
        
        // Show before and after
        System.out.println("=== ORIGINAL TEMPLATE ===");
        System.out.println("A customer named {name} sent the following message: \"{message}\"");
        System.out.println();
        
        System.out.println("=== VARIABLES MAP ===");
        variables.forEach((key, value) -> 
            System.out.println(key + " = " + value)
        );
        System.out.println();
        
        // Replace variables
        String result = template.create(variables).getContents();
        
        System.out.println("=== AFTER VARIABLE REPLACEMENT ===");
        System.out.println(result);
    }
}
