package com.ppfurtado.desafiovotacao.api.exception;

import com.ppfurtado.desafiovotacao.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class DummyController {
        @GetMapping("/test/notfound")
        public void notFound() { throw new ResourceNotFoundException("Not found"); }

        @GetMapping("/test/cpf")
        public void cpf() { throw new CpfInvalidoException("CPF inválido"); }

        @GetMapping("/test/ja-votou")
        public void jaVotou() { throw new AssociadoJaVotouException("Já votou"); }

        @GetMapping("/test/encerrada")
        public void encerrada() { throw new SessaoEncerradaException("Encerrada"); }

        @GetMapping("/test/nao-habilitado")
        public void naoHabilitado() { throw new AssociadoNaoHabilitadoException("Não habilitado"); }

        @GetMapping("/test/business")
        public void business() { throw new BusinessException("Negócio"); }

        @GetMapping("/test/generic")
        public void generic() { throw new RuntimeException("boom"); }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void resourceNotFound_returns404() throws Exception {
        mockMvc.perform(get("/test/notfound").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void cpfInvalido_returns404() throws Exception {
        mockMvc.perform(get("/test/cpf").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("CPF inválido"));
    }

    @Test
    void associadoJaVotou_returns409() throws Exception {
        mockMvc.perform(get("/test/ja-votou").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já votou"));
    }

    @Test
    void sessaoEncerrada_returns422() throws Exception {
        mockMvc.perform(get("/test/encerrada").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Encerrada"));
    }

    @Test
    void associadoNaoHabilitado_returns403() throws Exception {
        mockMvc.perform(get("/test/nao-habilitado").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Não habilitado"));
    }

    @Test
    void businessException_returns400() throws Exception {
        mockMvc.perform(get("/test/business").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Negócio"));
    }

    @Test
    void genericException_returns500() throws Exception {
        mockMvc.perform(get("/test/generic").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Ocorreu um erro interno inesperado no servidor."));
    }
}
