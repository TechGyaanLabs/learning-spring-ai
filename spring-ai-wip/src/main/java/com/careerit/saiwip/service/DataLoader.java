package com.careerit.saiwip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoader {

    @Value("classpath:budget_speech.pdf")
    private Resource budgetSpeech;

    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;
    private final TokenTextSplitter textSplitter;

    /**
     * Load documents from PDF into VectorStore
     */
    public void loadDocumentsFromPdf() {
        try {
            log.info("Starting to load documents from PDF...");
            
            // Check if VectorStore already has documents
            if (isVectorStoreEmpty()) {
                log.info("VectorStore is empty. Loading documents from PDF...");
                
                // Read PDF and extract documents
                List<Document> documents = extractDocumentsFromPdf();
                
                // Add documents to VectorStore
                vectorStore.add(documents);
                
                log.info("Successfully loaded {} documents from PDF into VectorStore", documents.size());
            } else {
                log.info("VectorStore already contains documents. Skipping PDF loading.");
            }
            
        } catch (Exception e) {
            log.error("Error loading documents from PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load documents from PDF", e);
        }
    }

    /**
     * Extract documents from PDF using page-based reading with text splitting
     */
    private List<Document> extractDocumentsFromPdf() throws IOException {
        // Configure PDF reader to read one page per document
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig
                .builder()
                .withPagesPerDocument(1)
                .build();

        // Use PagePdfDocumentReader for page-based extraction
        PagePdfDocumentReader reader = new PagePdfDocumentReader(budgetSpeech, config);
        
        // Get raw documents (one per page)
        List<Document> rawDocuments = reader.get();
        log.info("Extracted {} raw pages from PDF", rawDocuments.size());
        
        // Split documents using text splitter for better chunking
        List<Document> splitDocuments = textSplitter.apply(rawDocuments);
        
        // Add metadata to each document
        splitDocuments.forEach(doc -> {
            doc.getMetadata().put("source", "budget_speech.pdf");
            doc.getMetadata().put("type", "budget_document");
            doc.getMetadata().put("chunk_index", splitDocuments.indexOf(doc));
            doc.getMetadata().put("total_chunks", splitDocuments.size());
        });
        
        log.info("Split into {} document chunks using text splitter", splitDocuments.size());
        return splitDocuments;
    }

    /**
     * Check if VectorStore is empty
     */
    private boolean isVectorStoreEmpty() {
        try {
            List<Document> existingDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query("*")
                    .topK(1)
                    .build()
            );
            return existingDocs.isEmpty();
        } catch (Exception e) {
            log.warn("Could not check VectorStore status: {}", e.getMessage());
            return true; // Assume empty if we can't check
        }
    }

    /**
     * Search for documents in VectorStore
     */
    public List<Document> searchDocuments(String query, int topK) {
        try {
            return vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(query)
                    .topK(topK)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error searching documents: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search documents", e);
        }
    }

    /**
     * Get document count from VectorStore
     */
    public int getDocumentCount() {
        try {
            // Query the database directly to get count
            Integer count = jdbcClient.sql("SELECT COUNT(*) FROM vector_store")
                .query(Integer.class)
                .single();
            return count != null ? count : 0;
        } catch (Exception e) {
            log.warn("Could not get document count: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Clear all documents from VectorStore
     */
    public void clearVectorStore() {
        try {
            jdbcClient.sql("DELETE FROM vector_store").update();
            log.info("Cleared all documents from VectorStore");
        } catch (Exception e) {
            log.error("Error clearing VectorStore: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear VectorStore", e);
        }
    }

    /**
     * Reload documents from PDF (clears existing and loads new)
     */
    public void reloadDocumentsFromPdf() {
        log.info("Reloading documents from PDF...");
        clearVectorStore();
        loadDocumentsFromPdf();
    }

    
    /**
     * Search documents with chunk information
     */
    public List<Document> searchDocumentsWithChunkInfo(String query, int topK) {
        try {
            List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(query)
                    .topK(topK)
                    .build()
            );
            
            // Add chunk information to results
            results.forEach(doc -> {
                Map<String, Object> metadata = doc.getMetadata();
                if (metadata.containsKey("chunk_index") && metadata.containsKey("total_chunks")) {
                    int chunkIndex = (Integer) metadata.get("chunk_index");
                    int totalChunks = (Integer) metadata.get("total_chunks");
                    metadata.put("chunk_info", String.format("Chunk %d of %d", chunkIndex + 1, totalChunks));
                }
            });
            
            return results;
        } catch (Exception e) {
            log.error("Error searching documents with chunk info: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search documents with chunk info", e);
        }
    }
}