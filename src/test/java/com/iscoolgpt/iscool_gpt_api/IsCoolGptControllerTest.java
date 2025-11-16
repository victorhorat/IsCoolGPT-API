package com.iscoolgpt.iscool_gpt_api;

import com.iscoolgpt.iscool_gpt_api.controller.IsCoolGptController;
import com.iscoolgpt.iscool_gpt_api.service.LlmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IsCoolGptController.class)
class IsCoolGptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LlmService llmService;

    @Test
    void givenValidQuestion_whenAskQuestion_thenReturnsAnswer() throws Exception {
        Mockito.when(llmService.generateResponse(anyString()))
               .thenReturn("Resposta simulada!");

        mockMvc.perform(post("/api/v1/iscool/ask")
                .content("Qual o significado da vida?")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Resposta simulada!"));
    }

    @Test
    void givenEmptyQuestion_whenAskQuestion_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/iscool/ask")
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A pergunta não pode estar vazia."));
    }
}
