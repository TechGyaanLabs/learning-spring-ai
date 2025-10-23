package com.careerit.saiopenai.controller;

import com.careerit.saiopenai.service.ChatClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/openai")
@RequiredArgsConstructor
public class ChatClientController {

    private final ChatClientServiceImpl chatClientService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam("message") String message) {
        return ResponseEntity.ok(chatClientService.chat(message));
    }

}
