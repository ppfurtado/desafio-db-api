package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.response.ResultadoVotacaoResponse;
import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.repository.VotoRepository;
import com.ppfurtado.desafiovotacao.repository.projection.VotoContabilizadoProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private ResultadoVotacaoService resultadoVotacaoService;

    private SessaoVotacao sessao;

    @BeforeEach
    void setup() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Teste").build();
        sessao = SessaoVotacao.builder()
                .id(10L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.now().minusMinutes(5))
                .dataEncerramento(LocalDateTime.now().plusMinutes(5))
                .duracaoMinutos(10L)
                .build();
    }

    private VotoContabilizadoProjection proj(OpcaoVoto opcao, Long total) {
        return new VotoContabilizadoProjection() {
            @Override
            public OpcaoVoto getOpcao() {
                return opcao;
            }

            @Override
            public Long getTotal() {
                return total;
            }
        };
    }

    @Test
    void contabilizar_semVotos_retornaSemVotos() {
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.countVotosBySessaoIdGrouped(sessao.getId())).thenReturn(List.of());

        ResultadoVotacaoResponse resp = resultadoVotacaoService.contabilizarResultadoPorPauta(1L);

        assertEquals(0L, resp.getTotalVotos());
        assertEquals("0.00%", resp.getPercentualSim());
        assertEquals("0.00%", resp.getPercentualNao());
        assertEquals(com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao.SEM_VOTOS, resp.getResultado());
    }

    @Test
    void contabilizar_simMaioria_retornaAprovada() {
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.countVotosBySessaoIdGrouped(sessao.getId()))
                .thenReturn(List.of(proj(OpcaoVoto.SIM, 3L), proj(OpcaoVoto.NAO, 1L)));

        ResultadoVotacaoResponse resp = resultadoVotacaoService.contabilizarResultadoPorPauta(1L);

        assertEquals(4L, resp.getTotalVotos());
        assertEquals(3L, resp.getTotalVotosSim());
        assertEquals("75.00%", resp.getPercentualSim());
        assertEquals(com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao.APROVADA, resp.getResultado());
    }

    @Test
    void contabilizar_negaoMaioria_retornaRejeitada() {
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.countVotosBySessaoIdGrouped(sessao.getId()))
                .thenReturn(List.of(proj(OpcaoVoto.SIM, 1L), proj(OpcaoVoto.NAO, 4L)));

        ResultadoVotacaoResponse resp = resultadoVotacaoService.contabilizarResultadoPorPauta(1L);

        assertEquals(5L, resp.getTotalVotos());
        assertEquals(4L, resp.getTotalVotosNao());
        assertEquals("80.00%", resp.getPercentualNao());
        assertEquals(com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao.REJEITADA, resp.getResultado());
    }

    @Test
    void contabilizar_empate_retornaEmpate() {
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.countVotosBySessaoIdGrouped(sessao.getId()))
                .thenReturn(List.of(proj(OpcaoVoto.SIM, 2L), proj(OpcaoVoto.NAO, 2L)));

        ResultadoVotacaoResponse resp = resultadoVotacaoService.contabilizarResultadoPorPauta(1L);

        assertEquals(4L, resp.getTotalVotos());
        assertEquals(com.ppfurtado.desafiovotacao.domain.entity.ResultadoVotacao.EMPATE, resp.getResultado());
    }
}
