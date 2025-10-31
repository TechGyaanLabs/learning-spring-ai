package com.careerit.saiib.controller;

import com.careerit.saiib.service.DocumentLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data-loader")
@RequiredArgsConstructor
public class DocumentLoaderController {

        private final DocumentLoaderService documentLoaderService;

        @PostMapping("/load")
        public Map<String, String> loadDocuments() {
                documentLoaderService.loadDocumentsFromPdf();
                Map<String, String> res = new HashMap<>();
                res.put("status", "loaded");
                return res;
        }

        @DeleteMapping("/clear")
        public Map<String, String> clearVectorStore() {
                documentLoaderService.clearVectorStore();
                Map<String, String> res = new HashMap<>();
                res.put("status", "cleared");
                return res;
        }

        @PostMapping("/reload")
        public Map<String, String> reloadDocuments() {
                documentLoaderService.reloadDocumentsFromPdf();
                Map<String, String> res = new HashMap<>();
                res.put("status", "reloaded");
                return res;
        }
        
}
