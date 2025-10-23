package com.careerit.saiwip.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TemplateService {
    
    private final ResourcePatternResolver resourcePatternResolver;
    private final Map<String, Resource> templateCache = new ConcurrentHashMap<>();
    
    public TemplateService(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
        loadTemplates();
    }
    
    /**
     * Load all .st templates from classpath:templates/ directory
     */
    private void loadTemplates() {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:templates/*.st");
            for (Resource resource : resources) {
                String templateName = extractTemplateName(resource.getFilename());
                templateCache.put(templateName, resource);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load templates", e);
        }
    }
    
    /**
     * Get template by name (without .st extension)
     */
    public Resource getTemplate(String templateName) {
        Resource template = templateCache.get(templateName);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        return template;
    }
    
    /**
     * Get all available template names
     */
    public Map<String, String> getAllTemplates() {
        Map<String, String> templates = new HashMap<>();
        templateCache.forEach((name, resource) -> {
            templates.put(name, resource.getDescription());
        });
        return templates;
    }
    
    /**
     * Create a PromptTemplate (without variables)
     */
    public PromptTemplate createPromptTemplate(String templateName) {
        Resource template = getTemplate(templateName);
        return new PromptTemplate(template);
    }
    
    /**
     * Get formatted prompt with variables
     */
    public String getFormattedPrompt(String templateName, Map<String, Object> variables) {
        PromptTemplate template = createPromptTemplate(templateName);
        return template.create(variables).getContents();
    }
    
    /**
     * Extract template name from filename (remove .st extension)
     */
    private String extractTemplateName(String filename) {
        if (filename == null || !filename.endsWith(".st")) {
            return filename;
        }
        return filename.substring(0, filename.length() - 3);
    }
    
    /**
     * Reload templates (useful for development)
     */
    public void reloadTemplates() {
        templateCache.clear();
        loadTemplates();
    }
}
