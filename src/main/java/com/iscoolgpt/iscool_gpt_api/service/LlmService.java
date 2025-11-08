package com.iscoolgpt.iscool_gpt_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;



@Service
@Slf4j // Para logs
public class LlmService {

    private final WebClient webClient;

    // Valores injetados do application.properties
    @Value("${llm.api.key}")
    private String llmApiKey;

    // Configura o WebClient ao inicializar o serviço
    public LlmService(@Value("${llm.api.url}") String llmApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(llmApiUrl)
                .build();
    }

    /**
     * Envia o prompt do estudante para o LLM externo e retorna a resposta.
     * @param prompt A pergunta do estudante.
     * @return A resposta gerada pelo LLM.
     */
    public String generateResponse(String prompt) {
        log.info("Sending prompt to LLM: {}", prompt);

        // 1. Montar o corpo da requisição (Payload)
        // **NOTA**: Este é um JSON simplificado. Em um projeto real, você usaria DTOs (Data Transfer Objects)
        // e um formato específico exigido pelo provedor LLM (ex: OpenAI, Gemini).
        String requestBody = String.format("""
            {
              "model": "model-name-here",
              "messages": [
                {"role": "user", "content": "%s"}
              ]
            }
            """, prompt.replace("\"", "\\\"")); // Escapa aspas para o JSON

        try {
            // 2. Executar a requisição POST usando WebClient (Forma não-bloqueante)
            String response = webClient.post()
                    .header("Authorization", "Bearer " + llmApiKey) // Chave de API no Header
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class) // Espera o corpo da resposta como String
                    .block(); // .block() converte a chamada reativa para síncrona, simplificando o Controller

            // 3. Processar a resposta (Em um projeto real, você faria um parsing do JSON da resposta)
            // Por simplicidade, vamos apenas retornar o corpo completo da resposta:
            return response != null ? response : "Error: No response from LLM.";

        } catch (Exception e) {
            log.error("Error communicating with LLM API: {}", e.getMessage());
            return "Erro interno ao consultar o assistente.";
        }
    }
}