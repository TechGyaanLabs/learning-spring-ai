package com.careerit.saiwip.controller;

import com.careerit.saiwip.service.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/budget-speech")
@RequiredArgsConstructor
public class BudgetSpeechController {

    private final DataLoader dataLoader;
    private final ChatClient chatClient;

    /**
     * Load budget speech documents from PDF
     * POST /api/v1/budget-speech/load
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> loadBudgetDocuments() {
        try {
            log.info("Loading budget speech documents from PDF...");
            
            dataLoader.loadDocumentsFromPdf();
            
            int documentCount = dataLoader.getDocumentCount();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Budget speech documents loaded successfully",
                "documentCount", documentCount
            ));
            
        } catch (Exception e) {
            log.error("Error loading budget speech documents: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to load budget speech documents: " + e.getMessage()
            ));
        }
    }

    /**
     * Search budget speech documents using ChatClient for intelligent responses
     * GET /api/v1/budget-speech/search?query=searchTerm&topK=5&useChatClient=true
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchBudgetDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK,
            @RequestParam(defaultValue = "true") boolean useChatClient) {
        
        try {
            log.info("Searching budget speech documents with query: {} (useChatClient: {})", query, useChatClient);
            
            List<Document> results = dataLoader.searchDocuments(query, topK);
            
            if (useChatClient && !results.isEmpty()) {
                // Use ChatClient to provide intelligent response based on search results
                String context = results.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n\n"));
                
                String chatResponse = chatClient.prompt()
                    .system("""
                        You are a helpful assistant that analyzes budget speech documents. 
                        Based on the provided context from budget speech documents, answer the user's question.
                        Provide accurate, relevant information and cite specific details when possible.
                        If the context doesn't contain enough information to answer the question, say so clearly.
                        """)
                    .user("Context from budget speech documents:\n" + context + "\n\nUser question: " + query)
                    .call()
                    .content();
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "query", query,
                    "topK", topK,
                    "useChatClient", useChatClient,
                    "chatResponse", chatResponse,
                    "sourceDocuments", results.stream()
                        .map(doc -> Map.<String, Object>of(
                            "content", doc.getText(),
                            "metadata", doc.getMetadata()
                        ))
                        .collect(Collectors.toList()),
                    "resultCount", results.size()
                ));
            } else {
                // Return raw search results without ChatClient processing
                List<Map<String, Object>> formattedResults = results.stream()
                    .map(doc -> Map.<String, Object>of(
                        "content", doc.getText(),
                        "metadata", doc.getMetadata()
                    ))
                    .collect(Collectors.toList());
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "query", query,
                    "topK", topK,
                    "useChatClient", useChatClient,
                    "resultCount", results.size(),
                    "results", formattedResults
                ));
            }
            
        } catch (Exception e) {
            log.error("Error searching budget speech documents: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Search failed: " + e.getMessage(),
                "query", query,
                "useChatClient", useChatClient
            ));
        }
    }

    /**
     * Chat with budget speech documents using ChatClient
     * POST /api/v1/budget-speech/chat
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chatWithBudgetDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        
        try {
            log.info("Chatting with budget speech documents: {}", query);
            
            // First, search for relevant documents
            List<Document> results = dataLoader.searchDocuments(query, topK);
            
            if (results.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "query", query,
                    "chatResponse", "I couldn't find any relevant information in the budget speech documents for your query.",
                    "sourceDocuments", List.of(),
                    "resultCount", 0
                ));
            }
            
            // Create context from search results
            String context = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
            
            // Use ChatClient for intelligent response
            String chatResponse = chatClient.prompt()
                .system("""
                    You are an expert budget analyst assistant. You have access to budget speech documents and can provide detailed, accurate information about budget allocations, financial planning, government spending, and fiscal policies.
                    
                    Guidelines:
                    - Answer based ONLY on the provided context from budget speech documents
                    - Be specific and cite relevant details when possible
                    - If asked for numbers or specific allocations, provide them accurately
                    - If the context doesn't contain enough information, clearly state this
                    - Use a professional, informative tone
                    - Structure your response clearly with bullet points or paragraphs as appropriate
                    """)
                .user("Budget Speech Context:\n" + context + "\n\nQuestion: " + query)
                .call()
                .content();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "query", query,
                "topK", topK,
                "chatResponse", chatResponse,
                "sourceDocuments", results.stream()
                    .map(doc -> Map.<String, Object>of(
                        "content", doc.getText(),
                        "metadata", doc.getMetadata()
                    ))
                    .collect(Collectors.toList()),
                "resultCount", results.size()
            ));
            
        } catch (Exception e) {
            log.error("Error chatting with budget speech documents: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Chat failed: " + e.getMessage(),
                "query", query
            ));
        }
    }

    /**
     * Get document count
     * GET /api/v1/budget-speech/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getDocumentCount() {
        try {
            int count = dataLoader.getDocumentCount();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "documentCount", count
            ));
            
        } catch (Exception e) {
            log.error("Error getting document count: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get document count: " + e.getMessage()
            ));
        }
    }

    /**
     * Clear all documents
     * DELETE /api/v1/budget-speech/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearDocuments() {
        try {
            log.info("Clearing all budget speech documents...");
            
            dataLoader.clearVectorStore();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All documents cleared successfully"
            ));
            
        } catch (Exception e) {
            log.error("Error clearing documents: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to clear documents: " + e.getMessage()
            ));
        }
    }

    /**
     * Reload documents (clear and load)
     * POST /api/v1/budget-speech/reload
     */
    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reloadDocuments() {
        try {
            log.info("Reloading budget speech documents...");
            
            dataLoader.reloadDocumentsFromPdf();
            
            int documentCount = dataLoader.getDocumentCount();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Documents reloaded successfully",
                "documentCount", documentCount
            ));
            
        } catch (Exception e) {
            log.error("Error reloading documents: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to reload documents: " + e.getMessage()
            ));
        }
    }

    /**
     * Health check endpoint
     * GET /api/v1/budget-speech/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            int count = dataLoader.getDocumentCount();
            boolean isHealthy = count > 0;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "status", isHealthy ? "healthy" : "no documents loaded",
                "documentCount", count,
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "status", "unhealthy",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }

     /**
     * Get sample search queries
     * GET /api/v1/budget-speech/sample-queries
     */
    @GetMapping("/sample-queries")
    public ResponseEntity<Map<String, Object>> getSampleQueries() {
        List<String> basicQueries = List.of(
            "budget allocation",
            "financial planning",
            "government spending",
            "revenue generation",
            "economic growth",
            "infrastructure development",
            "healthcare funding",
            "education budget",
            "tax policy",
            "fiscal policy"
        );
        
        List<String> chatQueries = List.of(
            "What are the main budget allocations for healthcare?",
            "How much is allocated for infrastructure development?",
            "What is the government's approach to education funding?",
            "Explain the tax policy changes in this budget",
            "What are the key priorities for economic growth?",
            "How does this budget address inflation?",
            "What measures are taken for job creation?",
            "Summarize the main highlights of this budget speech",
            "What are the revenue generation strategies?",
            "How is fiscal deficit being managed?"
        );
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "basicQueries", basicQueries,
            "chatQueries", chatQueries,
            "endpoints", Map.of(
                "search", "GET /search?query={query}&topK={number}&useChatClient={true/false}",
                "chat", "POST /chat?query={query}&topK={number}"
            ),
            "message", "Use basicQueries for simple search, chatQueries for conversational AI responses"
        ));
    }
}
