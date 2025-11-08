package com.iscoolgpt.iscool_gpt_api.controller;


import com.iscoolgpt.iscool_gpt_api.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/iscool")
public class IsCoolGptController {

    private final LlmService llmService;

    // Injeção de dependência via construtor (Boa Prática)
    public IsCoolGptController(LlmService llmService) {
        this.llmService = llmService;
    }

    // Endpoint principal do assistente
    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestBody String question) {
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("A pergunta não pode estar vazia.");
        }

        // Chama o serviço LLM para processar a pergunta
        String answer = llmService.generateResponse(question);

        return ResponseEntity.ok(answer);
    }
}