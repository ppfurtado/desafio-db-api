package com.ppfurtado.desafiovotacao.service;

import com.ppfurtado.desafiovotacao.api.v1.dto.request.VotoCreateRequest;
import com.ppfurtado.desafiovotacao.domain.entity.OpcaoVoto;
import com.ppfurtado.desafiovotacao.domain.entity.Voto;
import com.ppfurtado.desafiovotacao.domain.exception.AssociadoJaVotouException;
import com.ppfurtado.desafiovotacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;


    @InjectMocks
    private VotoService votoService;

    @Test
    void registrarVoto_throwsWhenAlreadyVoted() {
        when(votoRepository.existsBySessaoIdAndAssociadoCpf(20L, "58498501061")).thenReturn(true);

        VotoCreateRequest req = VotoCreateRequest.builder().associadoCpf("58498501061").opcaoVoto(OpcaoVoto.SIM).build();

        assertThrows(AssociadoJaVotouException.class, () -> votoService.registrarVoto(20L, req));
    }
}
