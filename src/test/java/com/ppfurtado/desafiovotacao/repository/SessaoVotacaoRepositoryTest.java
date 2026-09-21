package com.ppfurtado.desafiovotacao.repository;

import com.ppfurtado.desafiovotacao.domain.entity.Pauta;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoRepositoryTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Test
    void findByPautaId_shouldReturnSessionForGivenPauta() {
        Pauta pauta = Pauta.builder().id(10L).titulo("Pauta").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(20L)
                .pauta(pauta)
                .dataAbertura(LocalDateTime.now())
                .dataEncerramento(LocalDateTime.now().plusMinutes(10))
                .duracaoMinutos(10L)
                .build();

        when(sessaoVotacaoRepository.findByPautaId(10L)).thenReturn(List.of(sessao));

        List<SessaoVotacao> result = sessaoVotacaoRepository.findByPautaId(10L);

        assertFalse(result.isEmpty());
        assertEquals(20L, result.get(0).getId());
    }

    @Test
    void existsByPautaId_shouldDetectDuplicatedSessions() {
        when(sessaoVotacaoRepository.existsByPautaId(10L)).thenReturn(true);
        when(sessaoVotacaoRepository.existsByPautaId(99L)).thenReturn(false);

        assertTrue(sessaoVotacaoRepository.existsByPautaId(10L));
        assertFalse(sessaoVotacaoRepository.existsByPautaId(99L));
    }
}
