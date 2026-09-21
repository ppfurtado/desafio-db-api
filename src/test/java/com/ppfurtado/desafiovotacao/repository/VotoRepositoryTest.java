package com.ppfurtado.desafiovotacao.repository;

import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.SessaoVotacao;
import com.ppfurtado.desafiovotacao.repository.projection.VotoContabilizadoProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoRepositoryTest {

    @Mock
    private VotoRepository votoRepository;

    @Test
    void existsBySessaoIdAndAssociadoCpf_shouldReturnTrueForDuplicatedVote() {
        when(votoRepository.existsBySessaoIdAndAssociadoCpf(1L, "12345678909")).thenReturn(true);
        when(votoRepository.existsBySessaoIdAndAssociadoCpf(1L, "98765432100")).thenReturn(false);

        assertTrue(votoRepository.existsBySessaoIdAndAssociadoCpf(1L, "12345678909"));
        assertFalse(votoRepository.existsBySessaoIdAndAssociadoCpf(1L, "98765432100"));
    }

    @Test
    void countBySessaoIdAndOpcaoVoto_shouldCountVotesByOption() {
        when(votoRepository.countBySessaoIdAndOpcaoVoto(1L, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countBySessaoIdAndOpcaoVoto(1L, OpcaoVoto.NAO)).thenReturn(1L);
        when(votoRepository.countBySessaoId(1L)).thenReturn(3L);

        assertEquals(2L, votoRepository.countBySessaoIdAndOpcaoVoto(1L, OpcaoVoto.SIM));
        assertEquals(1L, votoRepository.countBySessaoIdAndOpcaoVoto(1L, OpcaoVoto.NAO));
        assertEquals(3L, votoRepository.countBySessaoId(1L));
    }

    @Test
    void countVotosBySessaoIdGrouped_shouldGroupVotesByChoice() {
        SessaoVotacao sessao = SessaoVotacao.builder().id(1L).duracaoMinutos(5L).dataAbertura(LocalDateTime.now()).dataEncerramento(LocalDateTime.now().plusMinutes(5)).build();
        VotoContabilizadoProjection sim = new VotoContabilizadoProjection() {
            @Override public OpcaoVoto getOpcao() { return OpcaoVoto.SIM; }
            @Override public Long getTotal() { return 2L; }
        };
        VotoContabilizadoProjection nao = new VotoContabilizadoProjection() {
            @Override public OpcaoVoto getOpcao() { return OpcaoVoto.NAO; }
            @Override public Long getTotal() { return 1L; }
        };

        when(votoRepository.countVotosBySessaoIdGrouped(sessao.getId())).thenReturn(List.of(sim, nao));

        Map<OpcaoVoto, Long> contagem = votoRepository.countVotosBySessaoIdGrouped(sessao.getId())
                .stream()
                .collect(Collectors.toMap(VotoContabilizadoProjection::getOpcao, VotoContabilizadoProjection::getTotal));

        assertEquals(2L, contagem.get(OpcaoVoto.SIM));
        assertEquals(1L, contagem.get(OpcaoVoto.NAO));
    }
}
