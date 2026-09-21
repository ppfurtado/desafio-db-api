package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ppfurtado.desafiovotacao.api.v1.dto.request.AbrirSessaoRequest;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.StatusSessao;
import com.ppfurtado.desafiovotacao.service.ResultadoVotacaoService;
import com.ppfurtado.desafiovotacao.service.SessaoVotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoControllerTest {

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private ResultadoVotacaoService resultadoVotacaoService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SessaoVotacaoController(sessaoVotacaoService, resultadoVotacaoService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void abrirSessao_shouldReturnCreated() throws Exception {
        AbrirSessaoRequest request = new AbrirSessaoRequest(10L, 5L);
        Pauta pauta = Pauta.builder().id(10L).titulo("Pauta Sessao").descricao("Descrição").dataCriacao(LocalDateTime.now()).build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(20L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.now().minusMinutes(1))
                .dataEncerramento(LocalDateTime.now().plusMinutes(5))
                .duracaoMinutos(5L)
                .build();

        when(sessaoVotacaoService.abrirSessao(any(AbrirSessaoRequest.class))).thenReturn(sessao);

        mockMvc.perform(post("/v1/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.pautaId").value(10))
                .andExpect(jsonPath("$.status").value("ABERTA"));

        verify(sessaoVotacaoService).abrirSessao(any(AbrirSessaoRequest.class));
    }

    @Test
    void buscarPorId_shouldReturnSessao() throws Exception {
        Pauta pauta = Pauta.builder().id(11L).titulo("Pauta de Busca").descricao("Desc").dataCriacao(LocalDateTime.now()).build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(21L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.of(2026, 9, 9, 10, 0, 0))
                .dataEncerramento(LocalDateTime.of(2026, 9, 9, 10, 5, 0))
                .duracaoMinutos(5L)
                .build();

        when(sessaoVotacaoService.buscarPorId(21L)).thenReturn(sessao);

        mockMvc.perform(get("/v1/sessoes/21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(21))
                .andExpect(jsonPath("$.pautaTitulo").value("Pauta de Busca"));

        verify(sessaoVotacaoService).buscarPorId(21L);
    }

    @Test
    void obterResultado_shouldReturnResultado() throws Exception {
        ResultadoVotacaoResponse resultado = ResultadoVotacaoResponse.builder()
                .pautaId(12L)
                .pautaTitulo("Pauta Resultado")
                .sessaoId(22L)
                .statusSessao(StatusSessao.ENCERRADA)
                .totalVotos(6L)
                .totalVotosSim(4L)
                .totalVotosNao(2L)
                .percentualSim("66.7%")
                .percentualNao("33.3%")
                .resultado(ResultadoVotacao.APROVADA)
                .build();

        when(resultadoVotacaoService.contabilizarResultadoPorSessao(22L)).thenReturn(resultado);

        mockMvc.perform(get("/v1/sessoes/22/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessaoId").value(22))
                .andExpect(jsonPath("$.resultado").value("APROVADA"));

        verify(resultadoVotacaoService).contabilizarResultadoPorSessao(22L);
    }

    @Test
    void listarTodas_shouldReturnPage() throws Exception {
        Pauta pauta = Pauta.builder().id(12L).titulo("Pauta Lista").descricao("Desc").dataCriacao(LocalDateTime.now()).build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(23L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.of(2026, 9, 9, 11, 0, 0))
                .dataEncerramento(LocalDateTime.of(2026, 9, 9, 11, 10, 0))
                .duracaoMinutos(10L)
                .build();
        Page<SessaoVotacao> page = new PageImpl<>(List.of(sessao), PageRequest.of(0, 20), 1);

        when(sessaoVotacaoService.listarTodas(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/v1/sessoes")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(23))
                .andExpect(jsonPath("$.content[0].pautaTitulo").value("Pauta Lista"));

        verify(sessaoVotacaoService).listarTodas(any(), any(Pageable.class));
    }
}
