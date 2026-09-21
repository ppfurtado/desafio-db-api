package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ppfurtado.desafiovotacao.api.v1.dto.request.VotoCreateRequest;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.service.SessaoVotacaoService;
import com.ppfurtado.desafiovotacao.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VotoControllerTest {

    @Mock
    private VotoService votoService;

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new VotoController(votoService, sessaoVotacaoService)).build();
    }

    @Test
    void registrarVotoNaSessao_shouldReturnCreated() throws Exception {
        VotoCreateRequest request = new VotoCreateRequest("12345678909", OpcaoVoto.SIM);

        mockMvc.perform(post("/v1/sessoes/30/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(votoService).registrarVoto(eq(30L), any(VotoCreateRequest.class));
    }

    @Test
    void registrarVotoNaPauta_shouldReturnAccepted() throws Exception {
        VotoCreateRequest request = new VotoCreateRequest("98765432100", OpcaoVoto.NAO);
        Pauta pauta = Pauta.builder().id(8L).titulo("Pauta Voto Pauta").descricao("Desc").dataCriacao(LocalDateTime.now()).build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(31L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.now().minusMinutes(1))
                .dataEncerramento(LocalDateTime.now().plusMinutes(10))
                .duracaoMinutos(10L)
                .build();

        when(sessaoVotacaoService.buscarPorPautaId(8L)).thenReturn(sessao);

        mockMvc.perform(post("/v1/pautas/8/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(sessaoVotacaoService).buscarPorPautaId(8L);
        verify(votoService).registrarVoto(eq(31L), any(VotoCreateRequest.class));
    }
}
