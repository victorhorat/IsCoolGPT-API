package com.iscoolgpt.iscool_gpt_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j // Para logs
public class LlmService {

    private final WebClient webClient;
    private final String llmApiUrl; // Armazena a URL da API

    // Valores injetados do application.properties
    @Value("${llm.api.key}")
    private String llmApiKey;

    // Configura o WebClient ao inicializar o serviço
    public LlmService(@Value("${llm.api.url}") String llmApiUrl) {
        // Removemos o baseUrl daqui para construir a URL completa no método
        this.webClient = WebClient.builder()
                .build();
        this.llmApiUrl = llmApiUrl;
    }

    /**
     * Envia o prompt do estudante para o LLM externo (Gemini) e retorna a resposta.
     * @param prompt A pergunta do estudante.
     * @return A resposta gerada pelo LLM.
     */
    public String generateResponse(String prompt) {
        log.info("Enviando prompt para o Gemini: {}", prompt);

        // --- 1. Guardrails (System Prompt) ---
        // Define o comportamento e as regras do assistente
        String systemPrompt = "Você é o IsCoolGPT, um assistente inteligente focado exclusivamente em Cloud Computing, DevOps, AWS, CI/CD e tópicos de engenharia de software. Responda apenas a perguntas acadêmicas e técnicas. Recuse-se educadamente a responder perguntas sobre política, fofocas, ou qualquer assunto fora do escopo de estudos em tecnologia.";

        // --- 2. Montar o corpo da requisição (Payload do Gemini) ---
        // Incluindo Guardrails, Temperatura (0.2) e o prompt do usuário
        String requestBody = String.format("""
            {
              "systemInstruction": {
                "parts": [{"text": "%s"}]
              },
              "contents": [
                {"role": "user", "parts": [{"text": "%s"}]}
              ],
              "generationConfig": {
                "temperature": 0.2,
                "maxOutputTokens": 2048
              }
            }
            """, systemPrompt.replace("\"", "\\\""), prompt.replace("\"", "\\\""));

        try {
            // --- 3. Executar a requisição POST (Autenticação Corrigida) ---
            // O Gemini usa a chave de API como um parâmetro de query (?key=...)
            JsonNode response = webClient.post()
                    .uri(llmApiUrl + "?key=" + llmApiKey) // <-- MUDANÇA 1: Autenticação na URL
                    // (Removemos o .header("Authorization", "Bearer ..."))
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class) // <-- MUDANÇA 2: Recebe um Objeto JSON
                    .block(); // Converte a chamada para síncrona

            // --- 4. Processar a resposta (Parsing do JSON) ---
            // O texto da resposta do Gemini está aninhado dentro de "candidates"
            if (response != null && response.has("candidates")) {
                String answerText = response.path("candidates")
                                          .path(0)
                                          .path("content")
                                          .path("parts")
                                          .path(0)
                                          .path("text")
                                          .asText("Não foi possível processar a resposta do assistente.");
                
                log.info("Resposta recebida do Gemini.");
                return answerText;
            } else if (response != null && response.has("error")) {
                // Captura erros da API do Gemini (ex: chave inválida, modelo errado)
                String errorMsg = response.path("error").path("message").asText("Erro desconhecido da API.");
                log.error("Erro da API Gemini: {}", errorMsg);
                return "Erro da API do Assistente: " + errorMsg;
            }

            return "Erro: Resposta nula ou mal formatada do LLM.";

        } catch (Exception e) {
            log.error("Erro ao comunicar com a API do LLM: {}", e.getMessage());
            return "Erro interno ao consultar o assistente.";
        }
    }
}