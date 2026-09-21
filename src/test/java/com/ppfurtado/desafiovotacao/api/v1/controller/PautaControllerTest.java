package com.ppfurtado.desafiovotacao.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ppfurtado.desafiovotacao.api.v1.dto.request.PautaCreateRequest;
import com.ppfurtado.desafiovotacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.StatusSessao;
import com.ppfurtado.desafiovotacao.service.PautaService;
import com.ppfurtado.desafiovotacao.service.ResultadoVotacaoService;
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
class PautaControllerTest {

    @Mock
    private PautaService pautaService;

    @Mock
    private ResultadoVotacaoService resultadoVotacaoService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PautaController(pautaService, resultadoVotacaoService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void cadastrar_shouldReturnCreated() throws Exception {
        PautaCreateRequest request = new PautaCreateRequest("Pauta de Teste", "Descrição");
        Pauta pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta de Teste")
                .descricao("Descrição")
                .dataCriacao(LocalDateTime.of(2026, 9, 9, 10, 0, 0))
                .build();

        when(pautaService.cadastrar(any(PautaCreateRequest.class))).thenReturn(pauta);

        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Pauta de Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição"));

        verify(pautaService).cadastrar(any(PautaCreateRequest.class));
    }

    @Test
    void buscarPorId_shouldReturnPauta() throws Exception {
        Pauta pauta = Pauta.builder()
                .id(2L)
                .titulo("Pauta 2")
                .descricao("Detalhes")
                .dataCriacao(LocalDateTime.of(2026, 9, 9, 12, 0, 0))
                .build();

        when(pautaService.buscarPorId(2L)).thenReturn(pauta);

        mockMvc.perform(get("/v1/pautas/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.titulo").value("Pauta 2"));

        verify(pautaService).buscarPorId(2L);
    }

    @Test
    void listar_shouldReturnPageOfPautas() throws Exception {
        Pauta pauta = Pauta.builder()
                .id(3L)
                .titulo("Pauta 3")
                .descricao("Lista")
                .dataCriacao(LocalDateTime.of(2026, 9, 9, 13, 0, 0))
                .build();
        Page<Pauta> page = new PageImpl<>(List.of(pauta), PageRequest.of(0, 20), 1);

        when(pautaService.listar(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/v1/pautas")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[0].titulo").value("Pauta 3"));

        verify(pautaService).listar(any(Pageable.class));
    }

    @Test
    void obterResultado_shouldReturnResult() throws Exception {
        ResultadoVotacaoResponse resultado = ResultadoVotacaoResponse.builder()
                .pautaId(4L)
                .pautaTitulo("Pauta 4")
                .sessaoId(8L)
                .statusSessao(StatusSessao.ENCERRADA)
                .totalVotos(10L)
                .totalVotosSim(7L)
                .totalVotosNao(3L)
                .percentualSim("70.0%")
                .percentualNao("30.0%")
                .resultado(ResultadoVotacao.APROVADA)
                .build();

        when(resultadoVotacaoService.contabilizarResultadoPorPauta(4L)).thenReturn(resultado);

        mockMvc.perform(get("/v1/pautas/4/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId").value(4))
                .andExpect(jsonPath("$.pautaTitulo").value("Pauta 4"))
                .andExpect(jsonPath("$.resultado").value("APROVADA"));

        verify(resultadoVotacaoService).contabilizarResultadoPorPauta(4L);
    }
}
